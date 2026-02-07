package com.bindglam.goldengine.currency

import java.util.*

class CurrencyRegistryImpl : CurrencyRegistry {
    private val map = hashMapOf<String, Currency>()

    override fun register(currency: Currency) {
        if(map.containsKey(currency.id()))
            error("Already registered")
        map[currency.id()] = currency
    }

    override fun clear() {
        map.clear()
    }

    override fun get(id: String) = Optional.ofNullable(map[id])
    override fun entries() = map.values
}