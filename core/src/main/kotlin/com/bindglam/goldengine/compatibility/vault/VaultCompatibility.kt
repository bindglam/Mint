package com.bindglam.goldengine.compatibility.vault

import com.bindglam.goldengine.GoldEngine
import com.bindglam.goldengine.compatibility.Compatibility
import com.bindglam.goldengine.utils.logger
import com.bindglam.goldengine.utils.plugin
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority

object VaultCompatibility : Compatibility {
    override val requiredPlugin = "Vault"

    override fun start() {
        logger().info("Vault hooked!")

        Bukkit.getServicesManager().register(Economy::class.java, VaultEconomy, GoldEngine.instance().plugin(), ServicePriority.Normal)
    }

    override fun end() {
        Bukkit.getServicesManager().unregister(VaultEconomy)
    }
}