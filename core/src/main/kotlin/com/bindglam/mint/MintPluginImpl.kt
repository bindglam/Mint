package com.bindglam.mint

import com.bindglam.mint.listeners.PlayerJoinQuitListener
import com.bindglam.mint.manager.*
import com.bindglam.mint.utils.Constants
import com.bindglam.mint.utils.UpdateChecker
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class MintPluginImpl : JavaPlugin(), MintPlugin {
    companion object {
        private val CONFIG_FILE =  File("plugins/${Constants.PLUGIN_NAME}", "config.yml")
    }

    private val config = MintConfiguration(CONFIG_FILE)

    private val managers = listOf(
        DatabaseManagerImpl,
        CommandManager,
        CompatibilityManager,
        LanguageManager,
        CurrencyManagerImpl,
        AccountManagerImpl
    )

    private lateinit var metrics: Metrics

    override fun onEnable() {
        if(!CONFIG_FILE.parentFile.exists())
            CONFIG_FILE.parentFile.mkdirs()

        this.config.load()

        Mint.registerInstance(this)

        this.server.pluginManager.registerEvents(PlayerJoinQuitListener, this)

        this.metrics = Metrics(this, Constants.BSTATS_PLUGIN_ID)

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
        this.server.asyncScheduler.runNow(this) { _ -> checkUpdate() }
    }

    override fun onDisable() {
        this.managers.forEach { it.end(ContextImpl(this)) }
    }

    override fun reload() {
        this.logger.info("Reloading...")

        this.managers.filterIsInstance<Reloadable>().forEach { it.reload(ContextImpl(this)) }

        this.logger.info("Successfully reloaded!")
    }

    override fun config() = this.config
    override fun databaseManager() = DatabaseManagerImpl
    override fun accountManager() = AccountManagerImpl
    override fun currencyManager() = CurrencyManagerImpl
}