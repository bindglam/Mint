package com.bindglam.mint.account;

import com.bindglam.mint.Mint;
import com.bindglam.mint.account.log.TransactionLogger;
import com.bindglam.mint.currency.Currency;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Account interface
 *
 * @author bindglam
 */
public interface Account {
    /**
     * Get the uuid of the holder
     */
    @NotNull UUID holder();

    @NotNull TransactionLogger logger();

    /**
     * Get the balance of the account in the given currency
     *
     * @param currency currency
     */
    CompletableFuture<BigDecimal> getBalance(Currency currency);

    /**
     * Modify the balance of the account in the given currency
     *
     * @param operation operation
     * @param currency currency
     * @param value value
     */
    CompletableFuture<Operation.Result> modifyBalance(Operation operation, Currency currency, BigDecimal value);

    /**
     * Get the balance of the account in the default currency
     */
    default CompletableFuture<BigDecimal> getBalance() {
        return getBalance(Mint.instance().currencyManager().defaultCurrency());
    }

    /**
     * Modify the balance of the account in the default currency
     *
     * @param operation operation
     * @param value value
     */
    default CompletableFuture<Operation.Result> modifyBalance(Operation operation, BigDecimal value) {
        return modifyBalance(operation, Mint.instance().currencyManager().defaultCurrency(), value);
    }
}
