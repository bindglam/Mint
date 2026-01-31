package com.bindglam.goldengine.currency;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public record Currency(String id, CurrencyDisplay display) {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###.#");

    public static final String WON = "won";

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
