package com.bindglam.mint.manager;

import com.bindglam.mint.currency.Currency;
import com.bindglam.mint.currency.CurrencyRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * CurrencyManager interface
 *
 * @author bindglam
 */
public interface CurrencyManager {
    /**
     * Get the currency registry
     */
    @NotNull CurrencyRegistry registry();

    /**
     * Get the default currency
     */
    @NotNull Currency defaultCurrency();
}
