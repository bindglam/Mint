package com.bindglam.mint.database;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * ResourceConsumer interface
 *
 * @author bindglam
 */
@ApiStatus.Internal
@FunctionalInterface
public interface ResourceConsumer<C, E extends Exception> {
    void accept(@NotNull C connection) throws E;
}
