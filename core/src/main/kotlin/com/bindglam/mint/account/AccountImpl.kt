package com.bindglam.mint.account

import com.bindglam.mint.Mint
import com.bindglam.mint.account.log.TransactionLog
import com.bindglam.mint.account.log.TransactionLoggerImpl
import com.bindglam.mint.account.operation.Operation
import com.bindglam.mint.currency.Currency
import com.bindglam.mint.manager.AccountManagerImpl
import com.bindglam.mint.manager.CurrencyManagerImpl
import redis.clients.jedis.Jedis
import java.math.BigDecimal
import java.sql.Connection
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import java.util.concurrent.CompletableFuture

open class AccountImpl(private val holder: UUID) : Account {
    companion object {
        fun createTable(connection: Connection) {
            connection.createStatement().use { statement ->
                statement.execute("CREATE TABLE IF NOT EXISTS ${AccountManagerImpl.ACCOUNTS_TABLE_NAME}" +
                        "(holder VARCHAR(36), currency VARCHAR(32), balance DECIMAL(65, 5))")
            }
        }

        fun syncRedis(jedis: Jedis) {
            if(!jedis.exists("${AccountManagerImpl.ACCOUNTS_TABLE_NAME}:dirty")) return
            jedis.smembers("${AccountManagerImpl.ACCOUNTS_TABLE_NAME}:dirty").map { UUID.fromString(it) }.forEach { holder ->
                val account = AccountManagerImpl.getAccount(holder)
                CurrencyManagerImpl.registry().entries().forEach { currency ->
                    account.syncRedis(currency)
                }
            }
            jedis.del("${AccountManagerImpl.ACCOUNTS_TABLE_NAME}:dirty")
        }
    }

    private val logger = TransactionLoggerImpl(this)

    protected fun getBalanceInRedis(jedis: Jedis, currency: Currency): BigDecimal? {
        val key = "${AccountManagerImpl.ACCOUNTS_TABLE_NAME}:uuid_${holder}:currency_${currency.id()}"
        return if(jedis.exists(key))
            BigDecimal(jedis.get(key))
        else null
    }

    protected fun setBalanceInRedis(jedis: Jedis, currency: Currency, value: BigDecimal) {
        val key = "${AccountManagerImpl.ACCOUNTS_TABLE_NAME}:uuid_${holder}:currency_${currency.id()}"
        jedis.set(key, value.toString())
        jedis.sadd("${AccountManagerImpl.ACCOUNTS_TABLE_NAME}:dirty", holder.toString())
    }

    protected open fun getBalanceInSQL(connection: Connection, currency: Currency): BigDecimal {
        connection.prepareStatement(
            "SELECT * FROM ${AccountManagerImpl.ACCOUNTS_TABLE_NAME} WHERE holder = ? AND currency = ?"
        ).use { statement ->
            statement.setString(1, holder.toString())
            statement.setString(2, currency.id())

            statement.executeQuery().use { result ->
                return if(result.next()) result.getBigDecimal("balance")
                else {
                    connection.prepareStatement("INSERT INTO ${AccountManagerImpl.ACCOUNTS_TABLE_NAME} (holder, currency, balance) VALUES (?, ?, ?)").use { statement2 ->
                        statement2.setString(1, holder.toString())
                        statement2.setString(2, currency.id())
                        statement2.setBigDecimal(3, BigDecimal.ZERO)
                        statement2.executeUpdate()
                    }
                    BigDecimal.ZERO
                }
            }
        }
    }

    protected open fun getBalanceInternal(currency: Currency): BigDecimal {
        var result: BigDecimal? = null

        Mint.instance().databaseManager().redis()?.getResource { resource ->
            result = getBalanceInRedis(resource, currency)
        }

        if(result == null) {
            Mint.instance().databaseManager().sql().getResource { connection ->
                result = getBalanceInSQL(connection, currency)
            }

            Mint.instance().databaseManager().redis()?.getResource { resource ->
                setBalanceInRedis(resource, currency, result ?: BigDecimal.ZERO)
            }
        }

        return result ?: BigDecimal.ZERO
    }

    protected open fun modifyBalanceInternal(operation: Operation, currency: Currency, value: BigDecimal): Operation.Result {
        val result = operation.operate(getBalanceInternal(currency), value)

        if (result.isSuccess) {
            if(Mint.instance().databaseManager().redis() != null) {
                Mint.instance().databaseManager().redis()?.getResource { resource ->
                    setBalanceInRedis(resource, currency, result.result)
                }
            } else {
                Mint.instance().databaseManager().sql().getResource { connection ->
                    connection.prepareStatement("UPDATE ${AccountManagerImpl.ACCOUNTS_TABLE_NAME} SET balance = ? WHERE holder = ? AND currency = ?").use { statement ->
                        statement.setBigDecimal(1, result.result)
                        statement.setString(2, holder.toString())
                        statement.setString(3, currency.id())
                        statement.executeUpdate()
                    }
                }
            }
        }

        logger.log(TransactionLog(Timestamp.from(Instant.now()), operation, currency, result, value))

        return result
    }

    override fun syncRedis(currency: Currency) {
        if(Mint.instance().databaseManager().redis() != null) {
            var balance = BigDecimal.ZERO

            Mint.instance().databaseManager().redis()?.getResource { resource ->
                balance = getBalanceInRedis(resource, currency) ?: BigDecimal.ZERO
            }

            Mint.instance().databaseManager().sql().getResource { connection ->
                connection.prepareStatement("UPDATE ${AccountManagerImpl.ACCOUNTS_TABLE_NAME} SET balance = ? WHERE holder = ? AND currency = ?").use { statement ->
                    statement.setBigDecimal(1, balance)
                    statement.setString(2, holder.toString())
                    statement.setString(3, currency.id())
                    statement.executeUpdate()
                }
            }
        }
    }

    override fun getBalance(currency: Currency): CompletableFuture<BigDecimal> =
        CompletableFuture.supplyAsync { getBalanceInternal(currency) }

    override fun modifyBalance(operation: Operation, currency: Currency, value: BigDecimal): CompletableFuture<Operation.Result> =
        CompletableFuture.supplyAsync { modifyBalanceInternal(operation, currency, value) }

    override fun holder() = holder
    override fun logger() = logger
}