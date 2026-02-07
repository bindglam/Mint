package com.bindglam.goldengine.compatibility.vault

import com.bindglam.goldengine.GoldEngine
import com.bindglam.goldengine.account.Operation
import com.bindglam.goldengine.manager.AccountManagerImpl
import net.milkbowl.vault.economy.AbstractEconomy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit
import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.math.floor

object VaultEconomy : AbstractEconomy() {
    private val decimalFormat = DecimalFormat("###,###")

    override fun isEnabled() = true

    override fun getName(): String = "GoldEngine"

    override fun hasBankSupport(): Boolean = false

    override fun fractionalDigits(): Int = 0

    override fun format(amount: Double): String = decimalFormat.format(floor(amount))

    override fun currencyNamePlural(): String = ""

    override fun currencyNameSingular(): String = ""

    override fun hasAccount(playerName: String): Boolean = true

    override fun hasAccount(playerName: String, worldName: String): Boolean = true

    override fun getBalance(playerName: String): Double {
        val player = Bukkit.getOfflinePlayer(playerName)

        AccountManagerImpl.getAccount(player.uniqueId).get().use { account ->
            return account.balance().get().toDouble()
        }
    }

    override fun getBalance(playerName: String, world: String): Double = getBalance(playerName)

    override fun has(playerName: String, amount: Double): Boolean = getBalance(playerName) >= amount

    override fun has(playerName: String, worldName: String, amount: Double): Boolean = getBalance(playerName, worldName) >= amount

    override fun withdrawPlayer(playerName: String, amount: Double): EconomyResponse {
        val player = Bukkit.getOfflinePlayer(playerName)

        AccountManagerImpl.getAccount(player.uniqueId).get().use { account ->
            return if(account.balance().modify(BigDecimal.valueOf(amount), Operation.SUBTRACT))
                EconomyResponse(amount, account.balance().get().toDouble(), EconomyResponse.ResponseType.SUCCESS, null)
            else
                EconomyResponse(amount, account.balance().get().toDouble(), EconomyResponse.ResponseType.FAILURE, null)
        }
    }

    override fun withdrawPlayer(playerName: String, worldName: String, amount: Double): EconomyResponse = withdrawPlayer(playerName, amount)

    override fun depositPlayer(playerName: String, amount: Double): EconomyResponse {
        val player = Bukkit.getOfflinePlayer(playerName)

        AccountManagerImpl.getAccount(player.uniqueId).get().use { account ->
            return if(account.balance().modify(BigDecimal.valueOf(amount), Operation.ADD))
                EconomyResponse(amount, account.balance().get().toDouble(), EconomyResponse.ResponseType.SUCCESS, null)
            else
                EconomyResponse(amount, account.balance().get().toDouble(), EconomyResponse.ResponseType.FAILURE, null)
        }
    }

    override fun depositPlayer(playerName: String, worldName: String, amount: Double): EconomyResponse = depositPlayer(playerName, amount)

    override fun createBank(name: String, player: String): EconomyResponse =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")

    override fun deleteBank(name: String): EconomyResponse =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")

    override fun bankBalance(name: String): EconomyResponse =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")

    override fun bankHas(name: String, amount: Double): EconomyResponse =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")

    override fun bankWithdraw(name: String, amount: Double): EconomyResponse =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")

    override fun bankDeposit(name: String, amount: Double): EconomyResponse =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")

    override fun isBankOwner(name: String, playerName: String): EconomyResponse =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")

    override fun isBankMember(name: String, playerName: String): EconomyResponse =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented")

    override fun getBanks(): List<String> =
        listOf()

    override fun createPlayerAccount(playerName: String): Boolean {
        val player = Bukkit.getOfflinePlayer(playerName)

        AccountManagerImpl.getAccount(player.uniqueId).get().close()
        return true
    }

    override fun createPlayerAccount(playerName: String, worldName: String): Boolean = createPlayerAccount(playerName)
}