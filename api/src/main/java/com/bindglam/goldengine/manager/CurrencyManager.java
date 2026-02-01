package com.bindglam.goldengine.manager;

import com.bindglam.goldengine.currency.CurrencyRegistry;

/**
 * CurrencyManager interface
 *
 * @author bindglam
 */
public interface CurrencyManager extends Managerial, Reloadable {
    CurrencyRegistry registry();
}
