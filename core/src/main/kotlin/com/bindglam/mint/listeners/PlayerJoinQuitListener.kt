package com.bindglam.mint.listeners

import com.bindglam.mint.manager.AccountManagerImpl
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object PlayerJoinQuitListener : Listener {
    @EventHandler
    fun PlayerJoinEvent.onJoin() {
        AccountManagerImpl.registerCachedAccount(player.uniqueId)
    }

    @EventHandler
    fun PlayerQuitEvent.onQuit() {
        AccountManagerImpl.unregisterCachedAccount(player.uniqueId)
    }
}