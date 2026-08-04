package io.github.bindglam.mint.account;

import io.github.bindglam.mint.Mint;
import io.github.bindglam.mint.account.log.TransactionLogger;
import io.github.bindglam.mint.account.operation.Operation;
import io.github.bindglam.mint.currency.Currency;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents an economic account for a player, providing access to balances and transaction history.
 *
 * @author bindglam
 */
public interface Account {
    /**
     * Returns the UUID of the account holder.
     *
     * @return The holder's UUID
     */
    @NotNull UUID holder();

    /**
     * Returns the transaction logger for this account.
     *
     * @return The transaction logger
     */
    @NotNull TransactionLogger logger();

    /**
     * Retrieves the balance for a specific currency.
     *
     * @param currency The currency to query
     * @return A CompletableFuture containing the balance
     */
    CompletableFuture<BigDecimal> getBalance(Currency currency);

    /**
     * Modifies the balance by applying an operation with the specified value.
     *
     * @param operation The type of operation (DEPOSIT or WITHDRAW)
     * @param currency  The currency to modify
     * @param value     The value to apply
     * @return A CompletableFuture containing the operation result
     */
    CompletableFuture<Operation.Result> modifyBalance(Operation operation, Currency currency, BigDecimal value);

    /**
     * Retrieves the balance in the default currency.
     *
     * @return A CompletableFuture containing the balance
     */
    default CompletableFuture<BigDecimal> getBalance() {
        return getBalance(Mint.instance().currencyManager().defaultCurrency());
    }

    /**
     * Modifies the balance in the default currency.
     *
     * @param operation The type of operation (DEPOSIT or WITHDRAW)
     * @param value     The value to apply
     * @return A CompletableFuture containing the operation result
     */
    default CompletableFuture<Operation.Result> modifyBalance(Operation operation, BigDecimal value) {
        return modifyBalance(operation, Mint.instance().currencyManager().defaultCurrency(), value);
    }
}
