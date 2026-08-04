package io.github.bindglam.mint.compatibility.vault

import io.github.bindglam.mint.Mint
import io.github.bindglam.mint.account.operation.Operation
import io.github.bindglam.mint.manager.AccountManagerImpl
import io.github.bindglam.mint.utils.plugin
import net.milkbowl.vault2.economy.AccountPermission
import net.milkbowl.vault2.economy.Economy
import net.milkbowl.vault2.economy.EconomyResponse
import java.math.BigDecimal
import java.util.Optional
import java.util.UUID

object VaultEconomy : Economy { // TODO : refactoring
    override fun isEnabled(): Boolean = true
    override fun getName(): String = Mint.instance().plugin().name
    override fun hasSharedAccountSupport(): Boolean  = false
    override fun hasMultiCurrencySupport(): Boolean = true
    override fun fractionalDigits(pluginName: String): Int = -1
    override fun format(amount: BigDecimal): String = format("", amount)
    override fun format(pluginName: String, amount: BigDecimal): String = format("", amount, getDefaultCurrency(""))
    override fun format(amount: BigDecimal, currency: String): String = format("", amount, currency)
    override fun format(pluginName: String, amount: BigDecimal, currency: String): String = Mint.instance().currencyManager().registry().get(currency).orElseThrow().format(amount)
    override fun hasCurrency(currency: String): Boolean = Mint.instance().currencyManager().registry().get(currency).isPresent
    override fun getDefaultCurrency(pluginName: String): String = Mint.instance().currencyManager().defaultCurrency().id()
    override fun defaultCurrencyNamePlural(pluginName: String): String = Mint.instance().currencyManager().defaultCurrency().display().pluralName()
    override fun defaultCurrencyNameSingular(pluginName: String): String = Mint.instance().currencyManager().defaultCurrency().display().singularName()
    override fun currencies(): Collection<String> = Mint.instance().currencyManager().registry().entries().map { it.id() }
    override fun createAccount(uuid: UUID, name: String): Boolean = true
    override fun createAccount(accountID: UUID, name: String, player: Boolean): Boolean = true
    override fun createAccount(uuid: UUID, name: String, worldName: String): Boolean = true
    override fun createAccount(accountID: UUID, name: String, worldName: String, player: Boolean): Boolean = true
    override fun getUUIDNameMap(): Map<UUID, String> = mapOf()
    override fun getAccountName(uuid: UUID): Optional<String> = Optional.empty()
    override fun hasAccount(uuid: UUID): Boolean = true
    override fun hasAccount(uuid: UUID, worldName: String): Boolean = true
    override fun renameAccount(uuid: UUID, name: String): Boolean = false
    override fun renameAccount(pluginName: String, accountID: UUID, name: String): Boolean = false
    override fun deleteAccount(pluginName: String, accountID: UUID): Boolean = false
    override fun accountSupportsCurrency(pluginName: String, accountID: UUID, currency: String): Boolean = Mint.instance().currencyManager().registry().get(currency).isPresent
    override fun accountSupportsCurrency(pluginName: String, accountID: UUID, currency: String, world: String): Boolean = accountSupportsCurrency(pluginName, accountID, currency)
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
            EconomyResponse(amount, result.result(), EconomyResponse.ResponseType.SUCCESS, "")
        else
            EconomyResponse(amount, result.result(), EconomyResponse.ResponseType.FAILURE, "")
    }
    override fun withdraw(pluginName: String, uuid: UUID, worldName: String, amount: BigDecimal): EconomyResponse = withdraw(pluginName, uuid, amount)
    override fun withdraw(pluginName: String, uuid: UUID, worldName: String, currency: String, amount: BigDecimal): EconomyResponse {
        val account = AccountManagerImpl.getAccount(uuid)

        val result = account.modifyBalance(Operation.WITHDRAW, Mint.instance().currencyManager().registry().get(currency).orElseThrow(), amount).get()
        return if(result.isSuccess)
            EconomyResponse(amount, result.result(), EconomyResponse.ResponseType.SUCCESS, "")
        else
            EconomyResponse(amount, result.result(), EconomyResponse.ResponseType.FAILURE, "")
    }

    override fun deposit(pluginName: String, uuid: UUID, amount: BigDecimal): EconomyResponse {
        val account = AccountManagerImpl.getAccount(uuid)

        val result = account.modifyBalance(Operation.DEPOSIT, amount).get()
        return if(result.isSuccess)
            EconomyResponse(amount, result.result(), EconomyResponse.ResponseType.SUCCESS, "")
        else
            EconomyResponse(amount, result.result(), EconomyResponse.ResponseType.FAILURE, "")
    }
    override fun deposit(pluginName: String, uuid: UUID, worldName: String, amount: BigDecimal): EconomyResponse = deposit(pluginName, uuid, amount)
    override fun deposit(pluginName: String, uuid: UUID, worldName: String, currency: String, amount: BigDecimal): EconomyResponse {
        val account = AccountManagerImpl.getAccount(uuid)

        val result = account.modifyBalance(Operation.DEPOSIT, Mint.instance().currencyManager().registry().get(currency).orElseThrow(), amount).get()
        return if(result.isSuccess)
            EconomyResponse(amount, result.result(), EconomyResponse.ResponseType.SUCCESS, "")
        else
            EconomyResponse(amount, result.result(), EconomyResponse.ResponseType.FAILURE, "")
    }

    override fun createSharedAccount(pluginName: String, accountID: UUID, name: String, owner: UUID): Boolean = true
    override fun isAccountOwner(pluginName: String, accountID: UUID, uuid: UUID): Boolean = accountID == uuid
    override fun setOwner(pluginName: String, accountID: UUID, uuid: UUID): Boolean = false
    override fun isAccountMember(pluginName: String, accountID: UUID, uuid: UUID): Boolean = accountID == uuid
    override fun addAccountMember(pluginName: String, accountID: UUID, uuid: UUID): Boolean = false
    override fun addAccountMember(pluginName: String, accountID: UUID, uuid: UUID, vararg initialPermissions: AccountPermission): Boolean = false
    override fun removeAccountMember(pluginName: String, accountID: UUID, uuid: UUID): Boolean = false
    override fun hasAccountPermission(pluginName: String, accountID: UUID, uuid: UUID, permission: AccountPermission): Boolean = accountID == uuid
    override fun updateAccountPermission(pluginName: String, accountID: UUID, uuid: UUID, permission: AccountPermission, value: Boolean): Boolean = false
}