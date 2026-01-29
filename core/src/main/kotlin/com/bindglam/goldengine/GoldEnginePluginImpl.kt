package com.bindglam.goldengine

import com.bindglam.goldengine.database.Database
import com.bindglam.goldengine.listeners.PlayerJoinQuitListener
import com.bindglam.goldengine.manager.AccountManagerImpl
import com.bindglam.goldengine.manager.CommandManager
import com.bindglam.goldengine.manager.CompatibilityManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class GoldEnginePluginImpl : JavaPlugin(), GoldEnginePlugin {
    companion object {
        private val CONFIG_FILE =  File("plugins/GoldEngine", "config.yml")
    }

    private val config = GoldEngineConfiguration(CONFIG_FILE)

    private val managers = listOf(
        CommandManager,
        CompatibilityManager,
        AccountManagerImpl
    )

    private lateinit var database: Database

    override fun onEnable() {
        if(!CONFIG_FILE.parentFile.exists())
            CONFIG_FILE.parentFile.mkdirs()

        this.config.load()

        GoldEngine.registerInstance(this)

        this.server.pluginManager.registerEvents(PlayerJoinQuitListener, this)

        this.database = this.config.database.type.value().create(this.config)
        this.database.start()

        this.managers.forEach { it.start() }
    }

    override fun onDisable() {
        this.managers.forEach { it.end() }

        this.database.stop()
    }

    override fun config() = this.config
    override fun database() = this.database
    override fun accountManager() = AccountManagerImpl
}