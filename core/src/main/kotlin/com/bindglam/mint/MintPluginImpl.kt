package com.bindglam.mint

import com.bindglam.mint.database.Database
import com.bindglam.mint.listeners.PlayerJoinQuitListener
import com.bindglam.mint.manager.AccountManagerImpl
import com.bindglam.mint.manager.CommandManager
import com.bindglam.mint.manager.CompatibilityManager
import com.bindglam.mint.manager.ContextImpl
import com.bindglam.mint.manager.CurrencyManagerImpl
import com.bindglam.mint.manager.LanguageManager
import com.bindglam.mint.manager.Reloadable
import com.bindglam.mint.utils.UpdateChecker
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class MintPluginImpl : JavaPlugin(), MintPlugin {
    companion object {
        private val CONFIG_FILE =  File("plugins/Mint", "config.yml")
    }

    private val config = MintConfiguration(CONFIG_FILE)

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

        Mint.registerInstance(this)

        this.server.pluginManager.registerEvents(PlayerJoinQuitListener, this)

        this.database = this.config.database.type.value().create(this.config)
        this.database.start()

        this.managers.forEach { it.start(ContextImpl(this)) }

        fun checkUpdate() {
            val checker = UpdateChecker("bindglam", "Mint")

            if(checker.check(this.pluginMeta.version)) {
                logger.info("A new version of Mint is available!")
                logger.info("https://github.com/bindglam/Mint/releases")
            } else {
                logger.info("You are using the latest version of Mint!")
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