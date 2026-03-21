package com.bindglam.mint.manager

import com.bindglam.mint.compatibility.Compatibility
import com.bindglam.mint.compatibility.papi.PlaceholderAPICompatibility
import com.bindglam.mint.compatibility.vault.VaultCompatibility
import org.bukkit.Bukkit

object CompatibilityManager : Managerial {
    private val compatibilities = listOf(VaultCompatibility, PlaceholderAPICompatibility)

    private val enabledCompatibilities = arrayListOf<Compatibility>()

    override fun priority() = Managerial.Priority.of(Int.MIN_VALUE, Int.MAX_VALUE)

    override fun preload(context: Context) {
        enabledCompatibilities.addAll(compatibilities
            .filter { compat -> Bukkit.getPluginManager().isPluginEnabled(compat.requiredPlugin) })
        enabledCompatibilities.forEach { it.start(context) }
    }

    override fun end(context: Context) {
        enabledCompatibilities.forEach { it.end(context) }
        enabledCompatibilities.clear()
    }
}