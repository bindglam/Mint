package com.bindglam.goldengine.listeners

import com.bindglam.goldengine.manager.AccountManagerImpl
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object PlayerJoinQuitListener : Listener {
    @EventHandler
    fun PlayerJoinEvent.onJoin() {
        AccountManagerImpl.loadOnlineAccount(player)
    }

    @EventHandler
    fun PlayerQuitEvent.onJoin() {
        AccountManagerImpl.disposeOnlineAccount(player)
    }
}