package com.bindglam.mint.account

import com.bindglam.mint.account.log.TransactionLog
import com.bindglam.mint.account.log.TransactionLoggerImpl
import com.bindglam.mint.account.operation.Operation
import com.bindglam.mint.currency.Currency
import com.bindglam.mint.events.AccountOperationEvent
import com.bindglam.mint.manager.AccountManagerImpl
import com.bindglam.mint.manager.CurrencyManagerImpl
import com.bindglam.mint.manager.DatabaseManagerImpl
import redis.clients.jedis.Jedis
import redis.clients.jedis.params.SetParams
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

        fun persistRedisData(jedis: Jedis) {
            val dirtyKey = "${AccountManagerImpl.ACCOUNTS_TABLE_NAME}:dirty"

            // Process dirty accounts one at a time
            while (true) {
                // Atomic: Pop one UUID from dirty set
                val holder = UUID.fromString(jedis.spop(dirtyKey) ?: break)

                try {
                    val account = AccountManagerImpl.getAccount(holder) as AccountImpl

                    CurrencyManagerImpl.registry().entries().forEach { currency ->
                        account.persistRedisData(jedis, currency)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
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
        jedis.set(key, value.toString(), SetParams.setParams().ex(120L))
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
        return AccountLocks.withLock(holder) {
            var result: BigDecimal? = null

            DatabaseManagerImpl.redis()?.getResource { resource ->
                result = getBalanceInRedis(resource, currency)
            }

            if (result == null) {
                DatabaseManagerImpl.sql().getResource { connection ->
                    result = getBalanceInSQL(connection, currency)
                }

                DatabaseManagerImpl.redis()?.getResource { resource ->
                    setBalanceInRedis(resource, currency, result ?: BigDecimal.ZERO)
                }
            }

            result ?: BigDecimal.ZERO
        }
    }

    protected open fun modifyBalanceInternal(operation: Operation, currency: Currency, value: BigDecimal): Operation.Result {
        return AccountLocks.withLock(holder) {
            val result = operation.operate(getBalanceInternal(currency), value)

            if (result.isSuccess) {
                if (DatabaseManagerImpl.redis() != null) {
                    DatabaseManagerImpl.redis()?.getResource { resource ->
                        setBalanceInRedis(resource, currency, result.result)
                    }
                } else {
                    DatabaseManagerImpl.sql().getResource { connection ->
                        connection.prepareStatement("UPDATE ${AccountManagerImpl.ACCOUNTS_TABLE_NAME} SET balance = ? WHERE holder = ? AND currency = ?")
                            .use { statement ->
                                statement.setBigDecimal(1, result.result)
                                statement.setString(2, holder.toString())
                                statement.setString(3, currency.id())
                                statement.executeUpdate()
                            }
                    }
                }
            }

            logger.log(TransactionLog(Timestamp.from(Instant.now()), operation, currency, result, value))

            AccountOperationEvent(this, operation, value, result).callEvent()

            result
        }
    }

    private fun persistRedisData(jedis: Jedis, currency: Currency) {
        if(DatabaseManagerImpl.redis() != null) {
            AccountLocks.withLock(holder) {
                val balance = getBalanceInRedis(jedis, currency) ?: BigDecimal.ZERO

                DatabaseManagerImpl.sql().getResource { connection ->
                    connection.prepareStatement("UPDATE ${AccountManagerImpl.ACCOUNTS_TABLE_NAME} SET balance = ? WHERE holder = ? AND currency = ?")
                        .use { statement ->
                            statement.setBigDecimal(1, balance)
                            statement.setString(2, holder.toString())
                            statement.setString(3, currency.id())
                            statement.executeUpdate()
                        }
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