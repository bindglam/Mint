package com.bindglam.mint.account

import com.bindglam.mint.Mint
import com.bindglam.mint.account.log.Log
import com.bindglam.mint.account.log.TransactionLoggerImpl
import com.bindglam.mint.currency.Currency
import com.bindglam.mint.manager.AccountManagerImpl
import java.math.BigDecimal
import java.sql.Connection
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import java.util.concurrent.CompletableFuture

class AccountImpl(private val holder: UUID) : Account {
    companion object {
        fun createTable(connection: Connection) {
            connection.createStatement().use { statement ->
                statement.execute("CREATE TABLE IF NOT EXISTS ${AccountManagerImpl.ACCOUNTS_TABLE_NAME}" +
                        "(holder VARCHAR(36), currency VARCHAR(32), balance DECIMAL)")
            }
        }
    }

    private val logger = TransactionLoggerImpl(this)

    private fun isNeedToInsert(connection: Connection, currency: Currency): Boolean {
        connection.prepareStatement(
            "SELECT * FROM ${AccountManagerImpl.ACCOUNTS_TABLE_NAME} WHERE holder = ? AND currency = ?"
        ).use { statement ->
            statement.setString(1, holder.toString())
            statement.setString(2, currency.id())

            statement.executeQuery().use { result ->
                return !result.next()
            }
        }
    }

    private fun getSync(connection: Connection, currency: Currency): BigDecimal {
        connection.prepareStatement(
            "SELECT * FROM ${AccountManagerImpl.ACCOUNTS_TABLE_NAME} WHERE holder = ? AND currency = ?"
        ).use { statement ->
            statement.setString(1, holder.toString())
            statement.setString(2, currency.id())

            statement.executeQuery().use { result ->
                return if(result.next()) result.getBigDecimal("balance")
                else BigDecimal.ZERO
            }
        }
    }

    override fun getBalance(currency: Currency): CompletableFuture<BigDecimal> =
        CompletableFuture.supplyAsync {
            var balance = BigDecimal.ZERO
            Mint.instance().database().getConnection { balance = getSync(it, currency) }
            return@supplyAsync balance
        }

    override fun modifyBalance(operation: Operation, currency: Currency, value: BigDecimal): CompletableFuture<Operation.Result> =
        CompletableFuture.supplyAsync {
            var operationResult = Operation.Result.failure(BigDecimal.ZERO)

            Mint.instance().database().getConnection { connection ->
                operationResult = operation.operate(getSync(connection, currency), value)

                if(operationResult.isSuccess) {
                    val needToInsert = isNeedToInsert(connection, currency)
                    val query = if (needToInsert) {
                        "INSERT INTO ${AccountManagerImpl.ACCOUNTS_TABLE_NAME} (holder, currency, balance) VALUES (?, ?, ?)"
                    } else {
                        "UPDATE ${AccountManagerImpl.ACCOUNTS_TABLE_NAME} SET balance = ? WHERE holder = ? AND currency = ?"
                    }

                    connection.prepareStatement(query).use { statement ->
                        if (needToInsert) {
                            statement.setString(1, holder.toString())
                            statement.setString(2, currency.id())
                            statement.setBigDecimal(3, operationResult.result)
                        } else {
                            statement.setBigDecimal(1, operationResult.result)
                            statement.setString(2, holder.toString())
                            statement.setString(3, currency.id())
                        }
                        statement.executeUpdate()
                    }
                }

                logger.log(Log(Timestamp.from(Instant.now()), operation, currency, operationResult, value))
            }

            return@supplyAsync operationResult
        }

    override fun holder() = holder
    override fun logger() = logger
}