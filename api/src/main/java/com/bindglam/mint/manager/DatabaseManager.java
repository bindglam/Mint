package com.bindglam.mint.manager;

import com.bindglam.mint.database.Database;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.sql.Connection;
import java.sql.SQLException;

@ApiStatus.Internal
public interface DatabaseManager extends Managerial {
    @NotNull Database<Connection, SQLException> sql();

    @Nullable Database<Jedis, JedisException> redis();
}
