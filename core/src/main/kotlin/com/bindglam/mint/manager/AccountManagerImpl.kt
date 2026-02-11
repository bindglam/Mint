package com.bindglam.mint.manager

import com.bindglam.mint.Mint
import com.bindglam.mint.account.Account
import com.bindglam.mint.account.AccountImpl
import com.bindglam.mint.account.CachedAccount
import com.bindglam.mint.account.CachedAccountImpl
import com.bindglam.mint.account.log.TransactionLoggerImpl
import com.bindglam.mint.utils.Constants
import java.util.*
import java.util.concurrent.CompletableFuture

object AccountManagerImpl : AccountManager {
    const val ACCOUNTS_TABLE_NAME = "${Constants.PLUGIN_ID}_accounts"
    const val LOGS_TABLE_NAME = "${Constants.PLUGIN_ID}_logs"

    private val cachedAccounts = hashMapOf<UUID, CachedAccount>()

    override fun start(context: Context) {
        context.plugin().database().getConnection { connection ->
            AccountImpl.createTable(connection)
            TransactionLoggerImpl.createTable(connection)
        }
    }

    override fun end(context: Context) {
    }

    fun registerCachedAccount(uuid: UUID) {
        cachedAccounts[uuid] = CachedAccountImpl(uuid).also { account ->
            CurrencyManagerImpl.registry().entries().forEach { account.getBalance(it).thenAccept {} }
        }
    }

    fun unregisterCachedAccount(uuid: UUID) {
        cachedAccounts.remove(uuid)
    }

    override fun getAccount(uuid: UUID): Account = AccountImpl(uuid)
    override fun getCachedAccount(uuid: UUID): CachedAccount? = cachedAccounts[uuid]
}