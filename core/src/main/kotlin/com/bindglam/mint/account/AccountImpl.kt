package com.bindglam.mint.account

import com.bindglam.mint.Mint
import com.bindglam.mint.account.log.Log
import com.bindglam.mint.account.log.TransactionLoggerImpl
import com.bindglam.mint.account.operation.Operation
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

    private fun getInternal(connection: Connection, currency: Currency): BigDecimal {
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

    override fun getBalance(currency: Currency): CompletableFuture<BigDecimal> =
        CompletableFuture.supplyAsync {
            var balance = BigDecimal.ZERO
            Mint.instance().database().getConnection { balance = getInternal(it, currency) }
            return@supplyAsync balance
        }

    override fun modifyBalance(operation: Operation, currency: Currency, value: BigDecimal): CompletableFuture<Operation.Result> =
        CompletableFuture.supplyAsync {
            var operationResult = Operation.Result.failure(BigDecimal.ZERO)

            Mint.instance().database().getConnection { connection ->
                getInternal(connection, currency)

                connection.prepareStatement(operation.getQuery(AccountManagerImpl.ACCOUNTS_TABLE_NAME)).use { statement ->
                    operation.applyStatement(statement, holder, currency, value)

                    operationResult = if(statement.executeUpdate() >= 1)
                        Operation.Result.success(getInternal(connection, currency))
                    else
                        Operation.Result.failure(getInternal(connection, currency))
                }
            }

            logger.log(Log(Timestamp.from(Instant.now()), operation, currency, operationResult, value))

            return@supplyAsync operationResult
        }

    override fun holder() = holder
    override fun logger() = logger
}