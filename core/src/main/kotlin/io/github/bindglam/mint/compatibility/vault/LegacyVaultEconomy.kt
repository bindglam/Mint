package io.github.bindglam.mint.compatibility.vault

import io.github.bindglam.mint.Mint
import io.github.bindglam.mint.account.operation.Operation
import io.github.bindglam.mint.manager.AccountManagerImpl
import io.github.bindglam.mint.utils.plugin
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.math.BigDecimal

object LegacyVaultEconomy : Economy {
    override fun isEnabled() = true
    override fun getName(): String = Mint.instance().plugin().name
    override fun hasBankSupport(): Boolean = false
    override fun fractionalDigits(): Int = 0
    override fun format(amount: Double): String = Mint.instance().currencyManager().defaultCurrency().format(BigDecimal.valueOf(amount))
    override fun currencyNamePlural(): String = Mint.instance().currencyManager().defaultCurrency().display().pluralName
    override fun currencyNameSingular(): String = Mint.instance().currencyManager().defaultCurrency().display().singularName()

    override fun hasAccount(playerName: String) = true
    override fun hasAccount(player: OfflinePlayer) = true
    override fun hasAccount(playerName: String, worldName: String) = true
    override fun hasAccount(player: OfflinePlayer, worldName: String) = true

    override fun getBalance(playerName: String) = getBalance(Bukkit.getOfflinePlayer(playerName))
    override fun getBalance(player: OfflinePlayer) = AccountManagerImpl.getAccount(player.uniqueId).balance.get().toDouble()
    override fun getBalance(playerName: String, world: String) = getBalance(playerName)
    override fun getBalance(player: OfflinePlayer, world: String) = getBalance(player)

    override fun has(playerName: String, amount: Double) = getBalance(playerName) >= amount
    override fun has(player: OfflinePlayer, amount: Double) = getBalance(player) >= amount
    override fun has(playerName: String, worldName: String, amount: Double) = getBalance(playerName, worldName) >= amount
    override fun has(player: OfflinePlayer, worldName: String, amount: Double) = getBalance(player, worldName) >= amount

    override fun withdrawPlayer(playerName: String, amount: Double) = withdrawPlayer(Bukkit.getOfflinePlayer(playerName), amount)
    override fun withdrawPlayer(player: OfflinePlayer, amount: Double): EconomyResponse {
        val account = AccountManagerImpl.getAccount(player.uniqueId)
        val result = account.modifyBalance(Operation.WITHDRAW, BigDecimal.valueOf(amount)).get()
        return if(result.isSuccess)
            EconomyResponse(amount, result.result().toDouble(), EconomyResponse.ResponseType.SUCCESS, null)
        else
            EconomyResponse(amount, result.result().toDouble(), EconomyResponse.ResponseType.FAILURE, null)
    }
    override fun withdrawPlayer(playerName: String, worldName: String, amount: Double) = withdrawPlayer(playerName, amount)
    override fun withdrawPlayer(player: OfflinePlayer, worldName: String, amount: Double) = withdrawPlayer(player, amount)

    override fun depositPlayer(playerName: String, amount: Double): EconomyResponse = depositPlayer(Bukkit.getOfflinePlayer(playerName), amount)
    override fun depositPlayer(player: OfflinePlayer, amount: Double): EconomyResponse {
        val account = AccountManagerImpl.getAccount(player.uniqueId)
        val result = account.modifyBalance(Operation.DEPOSIT, BigDecimal.valueOf(amount)).get()
        return if(result.isSuccess)
            EconomyResponse(amount, result.result().toDouble(), EconomyResponse.ResponseType.SUCCESS, null)
        else
            EconomyResponse(amount, result.result().toDouble(), EconomyResponse.ResponseType.FAILURE, null)
    }
    override fun depositPlayer(playerName: String, worldName: String, amount: Double) = depositPlayer(playerName, amount)
    override fun depositPlayer(player: OfflinePlayer, worldName: String, amount: Double) = depositPlayer(player, amount)

    override fun createBank(name: String, player: String) = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")
    override fun createBank(name: String, player: OfflinePlayer) = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")

    override fun deleteBank(name: String) = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")

    override fun bankBalance(name: String) = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")

    override fun bankHas(name: String, amount: Double) = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")

    override fun bankWithdraw(name: String, amount: Double) = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")

    override fun bankDeposit(name: String, amount: Double) = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")

    override fun isBankOwner(name: String, playerName: String) = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")
    override fun isBankOwner(name: String, player: OfflinePlayer) = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")

    override fun isBankMember(name: String, playerName: String) = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")
    override fun isBankMember(name: String, player: OfflinePlayer) = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")

    override fun getBanks() = listOf<String>()

    override fun createPlayerAccount(playerName: String) = true
    override fun createPlayerAccount(player: OfflinePlayer) = true
    override fun createPlayerAccount(playerName: String, worldName: String) = createPlayerAccount(playerName)
    override fun createPlayerAccount(player: OfflinePlayer, worldName: String) = createPlayerAccount(player)
}