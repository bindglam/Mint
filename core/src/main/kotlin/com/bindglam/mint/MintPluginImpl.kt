package com.bindglam.mint

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

    val mintConfig = MintConfiguration(CONFIG_FILE)

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

        this.mintConfig.load()

        Mint.registerInstance(this)

        this.metrics = Metrics(this, Constants.BSTATS_PLUGIN_ID)

        this.managers.sortedByDescending { it.priority().start }.forEach { it.preload(Context(this)) }
        server.asyncScheduler.runNow(this) { _ ->
            this.managers.sortedByDescending { it.priority().start }.forEach { it.start(Context(this)) }
        }

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
        this.managers.sortedByDescending { it.priority().end }.forEach { it.end(Context(this)) }
    }

    override fun reload() {
        this.logger.info("Reloading...")

        this.mintConfig.load()

        this.managers.sortedByDescending { it.priority().start }.filterIsInstance<Reloadable>().forEach { it.reload(Context(this)) }

        this.logger.info("Successfully reloaded!")
    }

    override fun accountManager() = AccountManagerImpl
    override fun currencyManager() = CurrencyManagerImpl
}