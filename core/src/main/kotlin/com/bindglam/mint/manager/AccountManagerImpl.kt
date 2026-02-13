package com.bindglam.mint.manager

import com.bindglam.mint.Mint
import com.bindglam.mint.account.Account
import com.bindglam.mint.account.AccountImpl
import com.bindglam.mint.account.CachedAccount
import com.bindglam.mint.account.CachedAccountImpl
import com.bindglam.mint.account.log.TransactionLoggerImpl
import com.bindglam.mint.utils.Constants
import com.bindglam.mint.utils.plugin
import java.util.*
import java.util.concurrent.TimeUnit

object AccountManagerImpl : AccountManager {
    const val ACCOUNTS_TABLE_NAME = "${Constants.PLUGIN_ID}_accounts"
    const val LOGS_TABLE_NAME = "${Constants.PLUGIN_ID}_logs"

    private val cachedAccounts = hashMapOf<UUID, CachedAccount>()

    override fun start(context: Context) {
        context.plugin().databaseManager().sql().getResource { connection ->
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
        Mint.instance().databaseManager().redis()?.getResource { resource ->
            AccountImpl.syncRedis(resource)
        }
    }

    fun registerCachedAccount(uuid: UUID) {
        cachedAccounts[uuid] = CachedAccountImpl(uuid).also { account ->
            CurrencyManagerImpl.registry().entries().forEach { account.getBalance(it).thenAccept {} }
        }
    }

    fun unregisterCachedAccount(uuid: UUID) {
        cachedAccounts.remove(uuid)
    }

    override fun getAccount(uuid: UUID): Account = getCachedAccount(uuid) ?: AccountImpl(uuid)
    override fun getCachedAccount(uuid: UUID): CachedAccount? = cachedAccounts[uuid]
}