package com.bindglam.goldengine;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * GoldEnginePlugin interface provider
 *
 * @author bindglam
 */
public final class GoldEngine {
    private static GoldEnginePlugin instance;

    private GoldEngine() {
        throw new RuntimeException();
    }

    public static @NotNull GoldEnginePlugin instance() {
        if(GoldEngine.instance == null)
            throw new IllegalStateException("Not initialized");
        return GoldEngine.instance;
    }

    @ApiStatus.Internal
    static void registerInstance(@NotNull GoldEnginePlugin instance) {
        if(GoldEngine.instance != null)
            throw new IllegalStateException("Already initialized");
        GoldEngine.instance = instance;
    }
}
