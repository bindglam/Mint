package com.bindglam.mint.account.log

import com.bindglam.mint.account.AccountImpl
import com.bindglam.mint.account.operation.Operation
import com.bindglam.mint.manager.AccountManagerImpl
import com.bindglam.mint.manager.CurrencyManagerImpl
import com.bindglam.mint.manager.DatabaseManagerImpl
import org.jetbrains.annotations.Range
import org.jetbrains.annotations.Unmodifiable
import java.sql.Connection
import java.util.concurrent.CompletableFuture

class TransactionLoggerImpl(val account: AccountImpl) : TransactionLogger {
    companion object {
        fun createTable(connection: Connection) {
            connection.createStatement().use { statement ->
                statement.execute("CREATE TABLE IF NOT EXISTS ${AccountManagerImpl.LOGS_TABLE_NAME}" +
                        "(holder VARCHAR(36), timestamp TIMESTAMP, operation VARCHAR(32), currency VARCHAR(32), result_success BOOLEAN, result_result DECIMAL(65, 5), value DECIMAL(65, 5))")
            }
        }
    }

    fun log(log: TransactionLog) {
        DatabaseManagerImpl.sql().getResource { connection ->
            connection.prepareStatement("INSERT INTO ${AccountManagerImpl.LOGS_TABLE_NAME} (holder, timestamp, operation, currency, result_success, result_result, value) VALUES (?, ?, ?, ?, ?, ?, ?)").use { statement ->
                statement.setString(1, account.holder().toString())
                statement.setTimestamp(2, log.timestamp())
                statement.setString(3, log.operation().toString())
                statement.setString(4, log.currency().id())
                statement.setBoolean(5, log.result().success())
                statement.setBigDecimal(6, log.result().result())
                statement.setBigDecimal(7, log.value())
                statement.executeUpdate()
            }
        }
    }

    override fun retrieveLogs(pagination: TransactionLogger.Pagination): @Unmodifiable CompletableFuture<List<TransactionLog>> =
        CompletableFuture.supplyAsync {
            val list = arrayListOf<TransactionLog>()

            DatabaseManagerImpl.sql().getResource { connection ->
                connection.prepareStatement("SELECT * FROM ${AccountManagerImpl.LOGS_TABLE_NAME} WHERE holder = ? ORDER BY timestamp ASC LIMIT ${pagination.size()} OFFSET ${pagination.size()*pagination.page()}").use { statement ->
                    statement.setString(1, account.holder().toString())

                    statement.executeQuery().use { result ->
                        while(result.next()) {
                            list.add(
                                TransactionLog(
                                    result.getTimestamp("timestamp"), Operation.valueOf(result.getString("operation")),
                                    CurrencyManagerImpl.registry().get(result.getString("currency"))
                                        .orElse(null),
                                    Operation.Result(
                                        result.getBoolean("result_success"),
                                        result.getBigDecimal("result_result")
                                    ),
                                    result.getBigDecimal("value")
                                )
                            )
                        }
                    }
                }
            }

            return@supplyAsync list
        }

    override fun clear() {
        CompletableFuture.runAsync {
            DatabaseManagerImpl.sql().getResource { connection ->
                connection.prepareStatement("DELETE FROM ${AccountManagerImpl.LOGS_TABLE_NAME} WHERE holder = ?").use { statement ->
                    statement.setString(1, account.holder().toString())
                    statement.executeUpdate()
                }
            }
        }
    }
}