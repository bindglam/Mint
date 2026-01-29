package com.bindglam.goldengine.manager

import com.bindglam.goldengine.compatibility.Compatibility
import com.bindglam.goldengine.compatibility.vault.VaultCompatibility
import org.bukkit.Bukkit

object CompatibilityManager : Managerial {
    private val compatibilities = listOf(VaultCompatibility)

    private val enabledCompatibilities = arrayListOf<Compatibility>()

    override fun start() {
        enabledCompatibilities.addAll(compatibilities
            .filter { compat -> Bukkit.getPluginManager().isPluginEnabled(compat.requiredPlugin) })
        enabledCompatibilities.forEach { it.start() }
    }

    override fun end() {
        enabledCompatibilities.forEach { it.end() }
        enabledCompatibilities.clear()
    }
}