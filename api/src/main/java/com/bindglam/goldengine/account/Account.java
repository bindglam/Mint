package com.bindglam.goldengine.account;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Account interface
 *
 * @author bindglam
 */
public interface Account extends AutoCloseable {
    void save();

    /**
     * Get the uuid of the holder
     */
    @NotNull UUID holder();

    @NotNull Balance balance();
}
