package com.bindglam.goldengine.currency;

import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * A registry class that stores currency definitions.
 *
 * @author bindglam
 */
public interface CurrencyRegistry {
    /**
     * Register currency definition
     *
     * @param currency currency definition
     */
    void register(Currency currency);

    /**
     * Clear currency definitions
     */
    void clear();

    /**
     * get currency definition by id
     *
     * @param id currency definition id
     */
    Optional<Currency> get(String id);

    @Unmodifiable Collection<Currency> entries();
}
