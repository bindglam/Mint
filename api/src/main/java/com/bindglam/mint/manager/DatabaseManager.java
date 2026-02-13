package com.bindglam.mint.manager;

import com.bindglam.mint.database.Database;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;

import java.sql.Connection;

@ApiStatus.Internal
public interface DatabaseManager extends Managerial {
    @NotNull Database<Connection> sql();

    @Nullable Database<Jedis> redis();
}
