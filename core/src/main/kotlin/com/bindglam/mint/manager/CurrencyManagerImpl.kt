package com.bindglam.mint.manager

import com.bindglam.mint.Mint
import com.bindglam.mint.currency.Currency
import com.bindglam.mint.currency.CurrencyDisplay
import com.bindglam.mint.currency.CurrencyRegistryImpl
import com.bindglam.mint.utils.plugin
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object CurrencyManagerImpl : CurrencyManager {
    private val currenciesFolder = File("plugins/Mint/currencies")
    private val defaultCurrencies = listOf("won")

    private val registry = CurrencyRegistryImpl()

    override fun start(context: Context) {
        if(!currenciesFolder.exists()) {
            currenciesFolder.mkdirs()

            defaultCurrencies.forEach { name ->
                val file = File(currenciesFolder, "$name.yml")
                if(file.exists()) return@forEach

                file.createNewFile()
                context.plugin().plugin().getResource("currencies/$name.yml")?.copyTo(file.outputStream())
            }
        }

        fun loadCurrency(config: ConfigurationSection): Currency {
            fun loadDisplay(config: ConfigurationSection): CurrencyDisplay {
                return CurrencyDisplay(config.getString("plural-name"), config.getString("singular-name"))
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
    override fun defaultCurrency() = registry[Mint.config().economy.currency.defaultCurrency.value()].orElseThrow()!!
}