package com.bindglam.goldengine.manager;

import com.bindglam.goldengine.currency.Currency;
import com.bindglam.goldengine.currency.CurrencyRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * CurrencyManager interface
 *
 * @author bindglam
 */
public interface CurrencyManager extends Managerial, Reloadable {
    @NotNull CurrencyRegistry registry();

    @NotNull Currency defaultCurrency();
}
