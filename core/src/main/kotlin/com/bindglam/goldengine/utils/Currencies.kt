package com.bindglam.goldengine.utils

import com.bindglam.goldengine.GoldEngine
import com.bindglam.goldengine.currency.Currency

fun won(): Currency = GoldEngine.instance().currencyManager().registry().get(Currency.WON).orElseThrow()