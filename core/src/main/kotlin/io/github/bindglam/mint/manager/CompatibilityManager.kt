package io.github.bindglam.mint.manager

import io.github.bindglam.mint.compatibility.Compatibility
import io.github.bindglam.mint.compatibility.papi.PlaceholderAPICompatibility
import io.github.bindglam.mint.compatibility.vault.VaultCompatibility
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