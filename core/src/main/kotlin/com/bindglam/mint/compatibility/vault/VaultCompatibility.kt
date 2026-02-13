package com.bindglam.mint.compatibility.vault

import com.bindglam.mint.Mint
import com.bindglam.mint.compatibility.Compatibility
import com.bindglam.mint.utils.logger
import com.bindglam.mint.utils.plugin
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority

object VaultCompatibility : Compatibility {
    override val requiredPlugin = "Vault"

    override fun start() {
        if(!isNewVault()) {
            logger().info("Legacy Vault hooked!")

            Bukkit.getServicesManager().register(net.milkbowl.vault.economy.Economy::class.java, LegacyVaultEconomy, Mint.instance().plugin(), ServicePriority.Normal)
        } else {
            logger().info("New Vault hooked!")

            Bukkit.getServicesManager().register(net.milkbowl.vault2.economy.Economy::class.java, VaultEconomy, Mint.instance().plugin(), ServicePriority.Normal)
        }
    }

    override fun end() {
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