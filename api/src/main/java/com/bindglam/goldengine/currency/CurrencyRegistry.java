package com.bindglam.goldengine.currency;

import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public final class CurrencyRegistry {
    private final Map<String, Currency> map = new HashMap<>();

    public void register(Currency currency) {
        if(map.containsKey(currency.id()))
            throw new IllegalStateException("Already registered");
        map.put(currency.id(), currency);
    }

    public void clear() {
        map.clear();
    }

    public Optional<Currency> get(String id) {
        return Optional.ofNullable(map.get(id));
    }

    public @Unmodifiable Collection<Currency> entries() {
        return map.values();
    }
}
