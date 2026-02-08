package com.bindglam.mint.account

import com.alibaba.fastjson2.JSON
import com.bindglam.mint.Mint
import com.bindglam.mint.manager.AccountManager
import com.bindglam.mint.manager.AccountManagerImpl
import java.util.*

abstract class AbstractAccount(private val holder: UUID) : Account {
    private val balance = BalanceImpl()

    private var isJustCreated = false

    init {
        load()
    }

    override fun close() {
        save()
    }

    private fun load() {
        Mint.instance().database().getConnection { connection ->
            connection.prepareStatement(
                "SELECT * FROM ${AccountManagerImpl.ACCOUNTS_TABLE_NAME} WHERE holder = ?"
            ).use { statement ->
                statement.setString(1, holder.toString())

                statement.executeQuery().use { result ->
                    if (result.next()) {
                        balance.fromJson(JSON.parseObject(result.getString("balance")))

                        isJustCreated = false
                    } else {
                        isJustCreated = true
                    }
                }
            }
        }
    }

    override fun save() {
        Mint.instance().database().getConnection { connection ->
            val query = if (isJustCreated) {
                "INSERT INTO ${AccountManagerImpl.ACCOUNTS_TABLE_NAME} (holder, balance) VALUES (?, ?)"
            } else {
                "UPDATE ${AccountManagerImpl.ACCOUNTS_TABLE_NAME} SET balance = ? WHERE holder = ?"
            }

            connection.prepareStatement(query).use { statement ->
                if (isJustCreated) {
                    statement.setString(1, holder.toString())
                    statement.setString(2, balance.toJson().toString())
                } else {
                    statement.setString(1, balance.toJson().toString())
                    statement.setString(2, holder.toString())
                }
                statement.executeUpdate()
            }
        }
    }

    override fun holder() = holder
    override fun balance() = balance
}