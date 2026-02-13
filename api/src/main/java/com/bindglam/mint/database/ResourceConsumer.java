package com.bindglam.mint.database;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

/**
 * ResourceConsumer interface
 *
 * @author bindglam
 */
@ApiStatus.Internal
@FunctionalInterface
public interface ResourceConsumer<C> {
    void accept(@NotNull C connection) throws SQLException;
}
