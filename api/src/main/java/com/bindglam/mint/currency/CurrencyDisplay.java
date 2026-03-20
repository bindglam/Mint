package com.bindglam.mint.currency;

/**
 * Defines the display properties for a currency.
 *
 * @param pluralName   The name used when the amount is greater than 1
 * @param singularName The name used when the amount is 1 or less
 * @author bindglam
 */
public record CurrencyDisplay(
        String pluralName,
        String singularName
) {
}
