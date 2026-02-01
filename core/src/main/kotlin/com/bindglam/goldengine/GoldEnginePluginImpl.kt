package com.bindglam.goldengine

import com.bindglam.goldengine.database.Database
import com.bindglam.goldengine.listeners.PlayerJoinQuitListener
import com.bindglam.goldengine.manager.AccountManagerImpl
import com.bindglam.goldengine.manager.CommandManager
import com.bindglam.goldengine.manager.CompatibilityManager
import com.bindglam.goldengine.manager.ContextImpl
import com.bindglam.goldengine.manager.CurrencyManagerImpl
import com.bindglam.goldengine.manager.LanguageManager
import com.bindglam.goldengine.manager.Reloadable
import com.bindglam.goldengine.utils.UpdateChecker
import org.bukkit.Bukkit
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
        LanguageManager,
        CurrencyManagerImpl,
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

        this.managers.forEach { it.start(ContextImpl(this)) }

        fun checkUpdate() {
            val checker = UpdateChecker("bindglam", "GoldEngine")

            if(checker.check(this.pluginMeta.version)) {
                logger.info("A new version of GoldEngine is available!")
                logger.info("https://github.com/bindglam/GoldEngine/releases")
            } else {
                logger.info("You are using the latest version of GoldEngine!")
            }
        }
        Bukkit.getAsyncScheduler().runNow(this) { _ -> checkUpdate() }
    }

    override fun onDisable() {
        this.managers.forEach { it.end(ContextImpl(this)) }

        this.database.stop()
    }

    override fun reload() {
        this.logger.info("Reloading...")

        this.managers.filterIsInstance<Reloadable>().forEach { it.reload(ContextImpl(this)) }

        this.logger.info("Successfully reloaded!")
    }

    override fun config() = this.config
    override fun database() = this.database
    override fun accountManager() = AccountManagerImpl
    override fun currencyManager() = CurrencyManagerImpl
}