package com.bindglam.mint.currency;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Represents a currency definition with unique identifier and display settings.
 *
 * @param id      The unique identifier for this currency
 * @param display The display properties for this currency
 * @author bindglam
 */
public record Currency(String id, CurrencyDisplay display) {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###.#");

    /**
     * Formats the given amount as a human-readable string with the currency name.
     *
     * @param amount The amount to format
     * @return The formatted string with the currency name
     */
    public String format(BigDecimal amount) {
        String name;
        if(amount.compareTo(BigDecimal.ONE) <= 0)
            name = display().singularName();
        else
            name = display().pluralName();
        if(name.length() > 1)
            name = " " + name;

        return DECIMAL_FORMAT.format(amount) + name;
    }
}
