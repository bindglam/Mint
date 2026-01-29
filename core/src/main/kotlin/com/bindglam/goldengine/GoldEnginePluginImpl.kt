package com.bindglam.goldengine

import com.bindglam.goldengine.database.Database
import com.bindglam.goldengine.database.DatabaseType
import com.bindglam.goldengine.listeners.PlayerJoinQuitListener
import com.bindglam.goldengine.manager.AccountManagerImpl
import com.bindglam.goldengine.manager.CommandManager
import org.bukkit.plugin.java.JavaPlugin

class GoldEnginePluginImpl : JavaPlugin(), GoldEnginePlugin {
    private val managers = listOf(
        CommandManager,
        AccountManagerImpl
    )

    private lateinit var database: Database

    override fun onEnable() {
        saveDefaultConfig()

        GoldEngine.registerInstance(this)

        this.server.pluginManager.registerEvents(PlayerJoinQuitListener, this)

        val databaseConfig = this.config.getConfigurationSection("database")!!
        this.database = DatabaseType.valueOf(databaseConfig.getString("type")!!).create(databaseConfig)
        this.database.start()

        managers.forEach { it.start() }
    }

    override fun onDisable() {
        managers.forEach { it.end() }

        this.database.stop()
    }

    override fun database() = database
    override fun accountManager() = AccountManagerImpl
}