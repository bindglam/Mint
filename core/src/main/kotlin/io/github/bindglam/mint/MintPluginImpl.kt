package io.github.bindglam.mint

import io.github.bindglam.mint.manager.AccountManagerImpl
import io.github.bindglam.mint.manager.CommandManager
import io.github.bindglam.mint.manager.CompatibilityManager
import io.github.bindglam.mint.manager.Context
import io.github.bindglam.mint.manager.CurrencyManagerImpl
import io.github.bindglam.mint.manager.DatabaseManagerImpl
import io.github.bindglam.mint.manager.LanguageManager
import io.github.bindglam.mint.manager.Reloadable
import io.github.bindglam.mint.utils.BSTATS_PLUGIN_ID
import io.github.bindglam.mint.utils.PLUGIN_NAME
import io.github.bindglam.mint.utils.UpdateChecker
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class MintPluginImpl : JavaPlugin(), MintPlugin {
    companion object {
        private val CONFIG_FILE =  File("plugins/${PLUGIN_NAME}", "config.yml")
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

        this.metrics = Metrics(this, BSTATS_PLUGIN_ID)

        this.managers.sortedByDescending { it.priority().start }.forEach { it.preload(Context(this)) }
        server.asyncScheduler.runNow(this) { _ ->
            this.managers.sortedByDescending { it.priority().start }.forEach { it.start(Context(this)) }
        }

        fun checkUpdate() {
            val checker = UpdateChecker("bindglam", PLUGIN_NAME)

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

        this.managers.sortedByDescending { it.priority().start }.filterIsInstance<Reloadable>().forEach { it.reload(
            Context(this)
        ) }

        this.logger.info("Successfully reloaded!")
    }

    override fun accountManager() = AccountManagerImpl
    override fun currencyManager() = CurrencyManagerImpl
}