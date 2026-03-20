package com.bindglam.mint.manager;

import com.bindglam.mint.currency.Currency;
import com.bindglam.mint.currency.CurrencyRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * Manages currency definitions and provides access to the currency registry.
 *
 * @author bindglam
 */
public interface CurrencyManager {
    /**
     * Returns the currency registry containing all registered currencies.
     *
     * @return The currency registry
     */
    @NotNull CurrencyRegistry registry();

    /**
     * Returns the default currency used for operations when none is specified.
     *
     * @return The default currency
     */
    @NotNull Currency defaultCurrency();
}
