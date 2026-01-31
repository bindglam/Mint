package com.bindglam.goldengine.manager

import com.bindglam.goldengine.GoldEngine
import com.bindglam.goldengine.account.Account
import com.bindglam.goldengine.account.OfflineAccount
import com.bindglam.goldengine.account.OnlineAccount
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

object AccountManagerImpl : AccountManager {
    private val onlineAccounts = ConcurrentHashMap<UUID, OnlineAccount>()

    override fun start(context: Context) {
        context.plugin().database().getConnection { connection ->
            connection.createStatement().use { statement ->
                statement.execute("CREATE TABLE IF NOT EXISTS ${AccountManager.ACCOUNTS_TABLE_NAME}" +
                        "(holder VARCHAR(36) PRIMARY KEY, balance JSON)")
            }
        }

        Bukkit.getOnlinePlayers().forEach { loadOnlineAccount(it) }
    }

    override fun end(context: Context) {
        Bukkit.getOnlinePlayers().forEach { disposeOnlineAccount(it) }
    }

    fun loadOnlineAccount(player: Player) {
        CompletableFuture.runAsync {
            onlineAccounts[player.uniqueId] = OnlineAccount(player.uniqueId)
        }
    }

    fun disposeOnlineAccount(player: Player) {
        CompletableFuture.runAsync {
            onlineAccounts.remove(player.uniqueId)?.save()
        }
    }

    override fun getAccount(uuid: UUID): CompletableFuture<out Account> {
        return if(onlineAccounts.containsKey(uuid))
            CompletableFuture.completedFuture(getOnlineAccount(uuid)!!)
        else
            getOfflineAccount(uuid)
    }

    override fun getOnlineAccount(uuid: UUID): OnlineAccount? = onlineAccounts[uuid]

    override fun getOfflineAccount(uuid: UUID): CompletableFuture<OfflineAccount> =
        CompletableFuture.supplyAsync { OfflineAccount(uuid) }
}