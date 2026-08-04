package io.github.bindglam.mint.currency;

/**
 * Defines the display properties for a currency.
 *
 * @param displayName
 * @param pluralName   The name used when the amount is greater than 1
 * @param singularName The name used when the amount is 1 or less
 * @author bindglam
 */
public record CurrencyDisplay(
        String displayName,
        String pluralName,
        String singularName
) {
}
