package io.github.bindglam.mint.manager

import io.github.bindglam.mint.account.Account
import io.github.bindglam.mint.account.AccountImpl
import io.github.bindglam.mint.account.log.TransactionLoggerImpl
import io.github.bindglam.mint.utils.PLUGIN_ID
import io.github.bindglam.mint.utils.plugin
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import java.util.*
import java.util.concurrent.TimeUnit

object AccountManagerImpl : AccountManager, Managerial {
    const val ACCOUNTS_TABLE_NAME = "${PLUGIN_ID}_accounts"
    const val LOGS_TABLE_NAME = "${PLUGIN_ID}_logs"

    private var redisSyncTask: ScheduledTask? = null

    override fun priority() = Managerial.Priority.of(-1, 1)

    override fun start(context: Context) {
        DatabaseManagerImpl.sql().getResource { connection ->
            AccountImpl.createTable(connection)
            TransactionLoggerImpl.createTable(connection)
        }

        if(context.config().database.redis.enabled.value() && context.config().database.redis.syncInterval.value() > 0) {
            redisSyncTask = Bukkit.getAsyncScheduler().runAtFixedRate(context.plugin().plugin(), { _ -> syncAllRedis() },
                0L, context.config().database.redis.syncInterval.value().toLong(), TimeUnit.SECONDS)
        }
    }

    override fun end(context: Context) {
        redisSyncTask?.cancel()

        syncAllRedis()
    }

    private fun syncAllRedis() {
        DatabaseManagerImpl.redis()?.getResource { resource ->
            AccountImpl.persistRedisData(resource)
        }
    }

    override fun getAccount(uuid: UUID): Account = AccountImpl(uuid)
}