package com.bindglam.goldengine.currency;

import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * A registry class that stores currency definitions.
 *
 * @author bindglam
 */
public final class CurrencyRegistry {
    private final Map<String, Currency> map = new HashMap<>();

    /**
     * Register currency definition
     *
     * @param currency currency definition
     */
    public void register(Currency currency) {
        if(map.containsKey(currency.id()))
            throw new IllegalStateException("Already registered");
        map.put(currency.id(), currency);
    }

    /**
     * Clear currency definitions
     */
    public void clear() {
        map.clear();
    }

    /**
     * get currency definition by id
     *
     * @param id currency definition id
     */
    public Optional<Currency> get(String id) {
        return Optional.ofNullable(map.get(id));
    }

    public @Unmodifiable Collection<Currency> entries() {
        return map.values();
    }
}
