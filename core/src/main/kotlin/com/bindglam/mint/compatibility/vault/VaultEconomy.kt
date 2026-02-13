package com.bindglam.mint.compatibility.vault

import com.bindglam.mint.Mint
import com.bindglam.mint.account.operation.Operation
import com.bindglam.mint.manager.AccountManagerImpl
import net.milkbowl.vault2.economy.Economy
import net.milkbowl.vault2.economy.EconomyResponse
import java.math.BigDecimal
import java.util.Optional
import java.util.UUID

object VaultEconomy : Economy {
    override fun isEnabled(): Boolean = true
    override fun getName(): String = "GoldEngine"
    override fun hasBankSupport(): Boolean = false
    override fun hasMultiCurrencySupport(): Boolean = true
    override fun fractionalDigits(): Int = 0
    override fun format(amount: BigDecimal): String = format(amount, Mint.instance().currencyManager().defaultCurrency().id())
    override fun format(amount: BigDecimal, currency: String): String = Mint.instance().currencyManager().registry().get(currency).orElseThrow().format(amount)
    override fun hasCurrency(currency: String): Boolean = Mint.instance().currencyManager().registry().get(currency).isPresent
    override fun getDefaultCurrency(): String = Mint.config().economy.currency.defaultCurrency.value()
    override fun defaultCurrencyNamePlural(): String = Mint.instance().currencyManager().defaultCurrency().display().pluralName()
    override fun defaultCurrencyNameSingular(): String = Mint.instance().currencyManager().defaultCurrency().display().singularName()
    override fun currencies(): Collection<String> = Mint.instance().currencyManager().registry().entries().map { it.id() }
    override fun createAccount(uuid: UUID, name: String): Boolean = true
    override fun createAccount(uuid: UUID, name: String, worldName: String): Boolean = true
    override fun getUUIDNameMap(): Map<UUID, String> = mapOf()
    override fun getAccountName(uuid: UUID): Optional<String> = Optional.empty()
    override fun hasAccount(uuid: UUID): Boolean = true
    override fun hasAccount(uuid: UUID, worldName: String): Boolean = true
    override fun renameAccount(uuid: UUID, name: String): Boolean = false

    override fun getBalance(pluginName: String, uuid: UUID): BigDecimal = AccountManagerImpl.getAccount(uuid).balance.get()
    override fun getBalance(pluginName: String, uuid: UUID, world: String): BigDecimal = getBalance(pluginName, uuid)
    override fun getBalance(pluginName: String, uuid: UUID, world: String, currency: String): BigDecimal =
        AccountManagerImpl.getAccount(uuid).getBalance(Mint.instance().currencyManager().registry().get(currency).orElseThrow()).get()

    override fun has(pluginName: String, uuid: UUID, amount: BigDecimal): Boolean = getBalance(pluginName, uuid) >= amount
    override fun has(pluginName: String, uuid: UUID, worldName: String, amount: BigDecimal): Boolean = has(pluginName, uuid, amount)
    override fun has(pluginName: String, uuid: UUID, worldName: String, currency: String, amount: BigDecimal): Boolean = getBalance(pluginName, uuid, worldName, currency) >= amount

    override fun withdraw(pluginName: String, uuid: UUID, amount: BigDecimal): EconomyResponse {
        val account = AccountManagerImpl.getAccount(uuid)

        val result = account.modifyBalance(Operation.WITHDRAW, amount).get()
        return if(result.isSuccess)
            EconomyResponse(amount, result.result(), EconomyResponse.ResponseType.SUCCESS, null)
        else
            EconomyResponse(amount, result.result(), EconomyResponse.ResponseType.FAILURE, null)
    }
    override fun withdraw(pluginName: String, uuid: UUID, worldName: String, amount: BigDecimal): EconomyResponse = withdraw(pluginName, uuid, amount)
    override fun withdraw(pluginName: String, uuid: UUID, worldName: String, currency: String, amount: BigDecimal): EconomyResponse {
        val account = AccountManagerImpl.getAccount(uuid)

        val result = account.modifyBalance(Operation.WITHDRAW, Mint.instance().currencyManager().registry().get(currency).orElseThrow(), amount).get()
        return if(result.isSuccess)
            EconomyResponse(amount, result.result(), EconomyResponse.ResponseType.SUCCESS, null)
        else
            EconomyResponse(amount, result.result(), EconomyResponse.ResponseType.FAILURE, null)
    }

    override fun deposit(pluginName: String, uuid: UUID, amount: BigDecimal): EconomyResponse {
        val account = AccountManagerImpl.getAccount(uuid)

        val result = account.modifyBalance(Operation.DEPOSIT, amount).get()
        return if(result.isSuccess)
            EconomyResponse(amount, result.result(), EconomyResponse.ResponseType.SUCCESS, null)
        else
            EconomyResponse(amount, result.result(), EconomyResponse.ResponseType.FAILURE, null)
    }
    override fun deposit(pluginName: String, uuid: UUID, worldName: String, amount: BigDecimal): EconomyResponse = deposit(pluginName, uuid, amount)
    override fun deposit(pluginName: String, uuid: UUID, worldName: String, currency: String, amount: BigDecimal): EconomyResponse {
        val account = AccountManagerImpl.getAccount(uuid)

        val result = account.modifyBalance(Operation.DEPOSIT, Mint.instance().currencyManager().registry().get(currency).orElseThrow(), amount).get()
        return if(result.isSuccess)
            EconomyResponse(amount, result.result(), EconomyResponse.ResponseType.SUCCESS, null)
        else
            EconomyResponse(amount, result.result(), EconomyResponse.ResponseType.FAILURE, null)
    }

    override fun createBank(pluginName: String, name: String, uuid: UUID): Boolean = false
    override fun deleteBank(pluginName: String, uuid: UUID): Boolean = false
    override fun getBankUUIDNameMap(): Map<UUID, String> = mapOf()
    override fun getBankAccountName(uuid: UUID): String = ""
    override fun hasBankAccount(uuid: UUID): Boolean = false
    override fun bankSupportsCurrency(uuid: UUID, currency: String): Boolean = false
    override fun renameBankAccount(pluginName: String, uuid: UUID, name: String): Boolean = false
    override fun bankBalance(pluginName: String, uuid: UUID): BigDecimal = BigDecimal.ZERO
    override fun bankBalance(pluginName: String, uuid: UUID, currency: String): BigDecimal = BigDecimal.ZERO
    override fun bankHas(pluginName: String, uuid: UUID, amount: BigDecimal): Boolean = false
    override fun bankHas(pluginName: String, uuid: UUID, currency: String, amount: BigDecimal): Boolean = false
    override fun bankWithdraw(pluginName: String, uuid: UUID, amount: BigDecimal): EconomyResponse = EconomyResponse(BigDecimal.ZERO, BigDecimal.ZERO, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null)
    override fun bankWithdraw(pluginName: String, uuid: UUID, currency: String, amount: BigDecimal): EconomyResponse = EconomyResponse(BigDecimal.ZERO, BigDecimal.ZERO, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null)
    override fun bankDeposit(pluginName: String, uuid: UUID, amount: BigDecimal): EconomyResponse = EconomyResponse(BigDecimal.ZERO, BigDecimal.ZERO, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null)
    override fun bankDeposit(pluginName: String, uuid: UUID, currency: String, amount: BigDecimal): EconomyResponse = EconomyResponse(BigDecimal.ZERO, BigDecimal.ZERO, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null)
    override fun isBankOwner(uuid: UUID?, bankUUID: UUID?): Boolean = false
    override fun isBankMember(uuid: UUID?, bankUUID: UUID?): Boolean = false
    override fun getBanks(): Collection<UUID> = listOf()
}