package com.bindglam.goldengine.manager;

import com.bindglam.goldengine.currency.CurrencyRegistry;

public interface CurrencyManager extends Managerial, Reloadable {
    CurrencyRegistry registry();
}
