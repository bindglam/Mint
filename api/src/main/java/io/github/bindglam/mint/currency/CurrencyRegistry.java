package io.github.bindglam.mint.currency;

import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * A registry that manages currency definitions.
 *
 * @author bindglam
 */
public interface CurrencyRegistry {
    /**
     * Registers a currency definition in the registry.
     *
     * @param currency The currency to register
     * @throws IllegalStateException If a currency with the same ID is already registered
     */
    void register(Currency currency);

    /**
     * Removes all currency definitions from the registry.
     */
    void clear();

    /**
     * Retrieves a currency definition by its identifier.
     *
     * @param id The unique identifier of the currency
     * @return An Optional containing the currency if found, empty otherwise
     */
    Optional<Currency> get(String id);

    /**
     * Returns an unmodifiable collection of all registered currencies.
     *
     * @return An unmodifiable collection of all currencies
     */
    @Unmodifiable Collection<Currency> entries();
}
