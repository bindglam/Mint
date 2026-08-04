package io.github.bindglam.mint;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to the Mint plugin instance for external plugins.
 *
 * @author bindglam
 */
public final class Mint {
    private static MintPlugin instance;

    private Mint() {
        throw new RuntimeException();
    }

    /**
     * Returns the current Mint plugin instance.
     *
     * @return The Mint plugin instance
     * @throws IllegalStateException If the plugin has not been initialized
     */
    public static @NotNull MintPlugin instance() {
        if(Mint.instance == null)
            throw new IllegalStateException("Not initialized");
        return Mint.instance;
    }

    /**
     * Internal method to register the plugin instance.
     *
     * @param instance The plugin instance to register
     * @throws IllegalStateException If an instance is already registered
     */
    @ApiStatus.Internal
    static void registerInstance(@NotNull MintPlugin instance) {
        if(Mint.instance != null)
            throw new IllegalStateException("Already initialized");
        Mint.instance = instance;
    }
}
