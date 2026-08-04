package io.github.bindglam.mint.compatibility.vault

import io.github.bindglam.mint.Mint
import io.github.bindglam.mint.compatibility.Compatibility
import io.github.bindglam.mint.compatibility.printCompatIssue
import io.github.bindglam.mint.manager.Context
import io.github.bindglam.mint.manager.DatabaseManagerImpl
import io.github.bindglam.mint.utils.logger
import io.github.bindglam.mint.utils.plugin
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority

object VaultCompatibility : Compatibility {
    override val requiredPlugin = "Vault"

    override fun start(context: Context) {
        if(!isNewVault()) {
            logger().info("Legacy Vault hooked!")

            Bukkit.getServicesManager().register(Economy::class.java, LegacyVaultEconomy, Mint.instance().plugin(), ServicePriority.Normal)
        } else {
            logger().info("New Vault hooked!")

            Bukkit.getServicesManager().register(net.milkbowl.vault2.economy.Economy::class.java, VaultEconomy, Mint.instance().plugin(), ServicePriority.Normal)
        }

        if(context.config().database.sql.type.value() == DatabaseManagerImpl.SQLDatabaseType.MYSQL || context.config().database.redis.enabled.value()) {
            printCompatIssue()
        }
    }

    override fun end(context: Context) {
        Bukkit.getServicesManager().unregister(if(isNewVault()) VaultEconomy else LegacyVaultEconomy)
    }

    private fun isNewVault(): Boolean {
        try {
            Class.forName("net.milkbowl.vault2.economy.Economy")
            return true
        } catch (_: Exception) {
            return false
        }
    }
}