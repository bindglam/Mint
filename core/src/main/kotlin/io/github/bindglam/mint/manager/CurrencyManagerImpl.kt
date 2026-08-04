package io.github.bindglam.mint.manager

import io.github.bindglam.mint.Mint
import io.github.bindglam.mint.currency.Currency
import io.github.bindglam.mint.currency.CurrencyDisplay
import io.github.bindglam.mint.currency.CurrencyRegistryImpl
import io.github.bindglam.mint.utils.PLUGIN_NAME
import io.github.bindglam.mint.utils.plugin
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

object CurrencyManagerImpl : CurrencyManager, Managerial, Reloadable {
    private val currenciesFolder = File("plugins/${PLUGIN_NAME}/currencies")
    private val defaultCurrencies = listOf("won")

    private val registry = CurrencyRegistryImpl()

    override fun start(context: Context) {
        if(!currenciesFolder.exists()) {
            currenciesFolder.mkdirs()

            defaultCurrencies.forEach { name ->
                val file = File(currenciesFolder, "$name.yml")
                if(file.exists()) return@forEach

                try {
                    file.createNewFile()
                } catch (_: IOException) {
                    context.logger().warning("Failed to create default currency file $name.yml")
                    return@forEach
                }
                context.plugin().plugin().getResource("currencies/$name.yml")?.copyTo(file.outputStream())
            }
        }

        fun loadCurrency(config: ConfigurationSection): Currency {
            fun loadDisplay(config: ConfigurationSection): CurrencyDisplay {
                return CurrencyDisplay(config.getString("display-name") ?: error("Display name not found"), config.getString("plural-name") ?: error("Plural name not found"), config.getString("singular-name") ?: error("Singular name not found"))
            }

            return Currency(config.name, loadDisplay(config.getConfigurationSection("display")!!))
        }

        var cnt = 0
        currenciesFolder.listFiles().forEach { file ->
            val config = YamlConfiguration.loadConfiguration(file)

            config.getKeys(false).forEach { id ->
                registry.register(loadCurrency(config.getConfigurationSection(id)!!))
                cnt++
            }
        }
        context.logger().info("Loaded $cnt currencies")
    }

    override fun end(context: Context) {
        registry.clear()
    }

    override fun reload(context: Context) {
        end(context)
        start(context)
    }

    override fun registry() = registry
    override fun defaultCurrency() = registry[Mint.instance().plugin().mintConfig.economy.currency.defaultCurrency.value()]
        .orElseThrow { NoSuchElementException("Default currency not found") }!!
}