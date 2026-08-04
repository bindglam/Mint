package io.github.bindglam.mint.compatibility.papi

import io.github.bindglam.mint.Mint
import io.github.bindglam.mint.utils.plugin
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer

object MintExpansion : PlaceholderExpansion() {
    override fun getIdentifier(): String = Mint.instance().plugin().name.lowercase()
    override fun getAuthor(): String = Mint.instance().plugin().pluginMeta.authors[0]
    override fun getVersion(): String = Mint.instance().plugin().pluginMeta.version

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        player ?: return null
        val account = Mint.instance().accountManager().getAccount(player.uniqueId)

        if(params.startsWith("balance_")) {
            val currency = Mint.instance().currencyManager().registry().get(params.substring("balance_".length)).orElse(null) ?: return null

            return account.getBalance(currency).get().toString()
        } else if(params.startsWith("formatted_balance_")) {
            val currency = Mint.instance().currencyManager().registry().get(params.substring("formatted_balance_".length)).orElse(null) ?: return null

            return currency.format(account.getBalance(currency).get())
        }

        return null
    }
}