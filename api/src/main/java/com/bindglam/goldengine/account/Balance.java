package com.bindglam.goldengine.account;

import com.bindglam.goldengine.GoldEngine;
import com.bindglam.goldengine.currency.Currency;

import java.math.BigDecimal;

public interface Balance {
    /**
     * Get the balance of the account in the given currency
     *
     * @param currency currency
     */
    BigDecimal get(Currency currency);

    /**
     * Set the balance of the account in the given currency
     *
     * @param currency currency
     * @param balance balance
     */
    void set(Currency currency, BigDecimal balance);

    /**
     * Modify the balance of the account in the given currency
     *
     * @param currency currency
     * @param amount amount
     * @param operation operation
     */
    boolean modify(Currency currency, BigDecimal amount, Operation operation);

    /**
     * Get the balance of the account in the default currency
     */
    default BigDecimal get() {
        return get(GoldEngine.instance().currencyManager().defaultCurrency());
    }

    /**
     * Set the balance of the account in the default currency
     */
    default void set(BigDecimal balance) {
        set(GoldEngine.instance().currencyManager().defaultCurrency(), balance);
    }

    /**
     * Modify the balance of the account in the default currency
     *
     * @param amount amount
     * @param operation operation
     */
    default boolean modify(BigDecimal amount, Operation operation) {
        return modify(GoldEngine.instance().currencyManager().defaultCurrency(), amount, operation);
    }
}
