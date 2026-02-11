package com.bindglam.mint.account;

import com.bindglam.mint.Mint;
import com.bindglam.mint.currency.Currency;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

/**
 * CachedAccount interface
 *
 * @author bindglam
 */
public interface CachedAccount extends Account {
    @Nullable BigDecimal getCachedBalance(Currency currency);

    default @Nullable BigDecimal getCachedBalance() {
        return getCachedBalance(Mint.instance().currencyManager().defaultCurrency());
    }
}
