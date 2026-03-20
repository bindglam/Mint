package com.bindglam.mint.manager

import com.bindglam.mint.account.Account
import com.bindglam.mint.account.AccountImpl
import com.bindglam.mint.account.log.TransactionLoggerImpl
import com.bindglam.mint.utils.Constants
import com.bindglam.mint.utils.plugin
import java.util.*
import java.util.concurrent.TimeUnit

object AccountManagerImpl : AccountManager, Managerial {
    const val ACCOUNTS_TABLE_NAME = "${Constants.PLUGIN_ID}_accounts"
    const val LOGS_TABLE_NAME = "${Constants.PLUGIN_ID}_logs"

    override fun priority() = Managerial.Priority.of(-1, 1)

    override fun start(context: Context) {
        DatabaseManagerImpl.sql().getResource { connection ->
            AccountImpl.createTable(connection)
            TransactionLoggerImpl.createTable(connection)
        }

        if(context.config().database.redis.enabled.value() && context.config().database.redis.syncInterval.value() > 0) {
            context.plugin().plugin().server.asyncScheduler.runAtFixedRate(context.plugin().plugin(), { _ ->
                syncAllRedis()
            }, 0L, context.config().database.redis.syncInterval.value().toLong(), TimeUnit.SECONDS)
        }
    }

    override fun end(context: Context) {
        syncAllRedis()
    }

    private fun syncAllRedis() {
        DatabaseManagerImpl.redis()?.getResource { resource ->
            AccountImpl.syncRedis(resource)
        }
    }

    override fun getAccount(uuid: UUID): Account = AccountImpl(uuid)
}