package com.bindglam.goldengine.account

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import com.bindglam.goldengine.GoldEngine
import com.bindglam.goldengine.currency.Currency
import com.bindglam.goldengine.manager.AccountManager
import java.math.BigDecimal
import java.util.*
import java.util.function.Consumer

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
        GoldEngine.instance().database().getConnection { connection ->
            connection.prepareStatement(
                "SELECT * FROM ${AccountManager.ACCOUNTS_TABLE_NAME} WHERE holder = ?"
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
        GoldEngine.instance().database().getConnection { connection ->
            val query = if (isJustCreated) {
                "INSERT INTO ${AccountManager.ACCOUNTS_TABLE_NAME} (holder, balance) VALUES (?, ?)"
            } else {
                "UPDATE ${AccountManager.ACCOUNTS_TABLE_NAME} SET balance = ? WHERE holder = ?"
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