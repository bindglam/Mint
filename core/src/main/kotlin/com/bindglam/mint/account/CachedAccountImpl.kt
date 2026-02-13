package com.bindglam.mint.account

import com.bindglam.mint.account.operation.Operation
import com.bindglam.mint.currency.Currency
import java.math.BigDecimal
import java.sql.Connection
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class CachedAccountImpl(holder: UUID) : AccountImpl(holder), CachedAccount {
    private val cache = ConcurrentHashMap<String, BigDecimal>()

    override fun getBalanceInternal(currency: Currency): BigDecimal =
        super.getBalanceInternal(currency).also { cache[currency.id()] = it }
    override fun modifyBalanceInternal(operation: Operation, currency: Currency, value: BigDecimal): Operation.Result =
        super.modifyBalanceInternal(operation, currency, value).also { cache[currency.id()] = it.result() }

    override fun getCachedBalance(currency: Currency): BigDecimal? = cache[currency.id()]
    override fun getBalance(currency: Currency): CompletableFuture<BigDecimal> =
        getCachedBalance(currency)?.let { CompletableFuture.completedFuture(it) } ?: super<AccountImpl>.getBalance(currency)
    override fun modifyBalance(operation: Operation, currency: Currency, value: BigDecimal): CompletableFuture<Operation.Result> {
        cache[currency.id()] = operation.operate(cache[currency.id()] ?: BigDecimal.ZERO, value).result()

        return super<AccountImpl>.modifyBalance(operation, currency, value)
    }
}