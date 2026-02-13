package com.bindglam.mint.database;

import org.jetbrains.annotations.ApiStatus;

/**
 * Database interface
 *
 * @author bindglam
 */
@ApiStatus.Internal
public interface Database<C, E extends Exception> {
    void start();

    void stop();

    void getResource(ResourceConsumer<C, E> consumer);
}
