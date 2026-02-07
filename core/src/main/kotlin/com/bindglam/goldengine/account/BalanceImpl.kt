package com.bindglam.goldengine.account

import com.alibaba.fastjson2.JSONObject
import com.bindglam.goldengine.GoldEngine
import com.bindglam.goldengine.currency.Currency
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

class BalanceImpl : Balance {
    private val map = ConcurrentHashMap<String, BigDecimal>()

    init {
        GoldEngine.instance().currencyManager().registry().entries().forEach { currency ->
            map[currency.id] = BigDecimal.ZERO
        }
    }

    fun fromJson(json: JSONObject) {
        GoldEngine.instance().currencyManager().registry().entries().forEach { currency ->
            map[currency.id] = json.getBigDecimal(currency.id) ?: BigDecimal.ZERO
        }
    }

    fun toJson(): JSONObject {
        return JSONObject().also {
            GoldEngine.instance().currencyManager().registry().entries().forEach { currency ->
                it[currency.id] = get(currency)
            }
        }
    }

    override fun get(currency: Currency): BigDecimal = map[currency.id] ?: BigDecimal.ZERO
    override fun set(currency: Currency, balance: BigDecimal) {
        map[currency.id] = balance
    }
    override fun modify(currency: Currency, amount: BigDecimal, operation: Operation): Boolean {
        val result = operation.operate(get(currency), amount)
        if (result.isFailed) return false
        set(currency, result.result)
        return true
    }
}