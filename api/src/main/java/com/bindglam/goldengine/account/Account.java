package com.bindglam.goldengine.account;

import com.bindglam.goldengine.GoldEngine;
import com.bindglam.goldengine.currency.Currency;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Account interface
 *
 * @author bindglam
 */
public interface Account extends AutoCloseable {
    /**
     * Get the uuid of the holder
     */
    @NotNull UUID holder();

    /**
     * Get the balance of the account in the given currency
     *
     * @param currency currency
     */
    BigDecimal balance(Currency currency);

    /**
     * Set the balance of the account in the given currency
     *
     * @param currency currency
     * @param balance balance
     */
    void balance(Currency currency, BigDecimal balance);

    /**
     * Modify the balance of the account in the given currency
     *
     * @param currency currency
     * @param amount amount
     * @param operation operation
     */
    boolean modifyBalance(Currency currency, BigDecimal amount, Operation operation);

    @Deprecated
    default BigDecimal balance() {
        return balance(GoldEngine.instance().currencyManager().registry().get(Currency.WON).orElseThrow());
    }

    @Deprecated
    default void balance(BigDecimal balance) {
        balance(GoldEngine.instance().currencyManager().registry().get(Currency.WON).orElseThrow(), balance);
    }

    @Deprecated
    default boolean modifyBalance(BigDecimal amount, Operation operation) {
        return modifyBalance(GoldEngine.instance().currencyManager().registry().get(Currency.WON).orElseThrow(), amount, operation);
    }
}
