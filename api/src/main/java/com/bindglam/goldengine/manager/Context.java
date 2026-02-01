package com.bindglam.goldengine.manager;

import com.bindglam.goldengine.GoldEngineConfiguration;
import com.bindglam.goldengine.GoldEnginePlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Context interface for managers
 *
 * @author bindglam
 */
public interface Context {
    @NotNull GoldEnginePlugin plugin();

    @NotNull GoldEngineConfiguration config();

    @NotNull Logger logger();
}
