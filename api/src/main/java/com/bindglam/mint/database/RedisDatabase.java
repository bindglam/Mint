package com.bindglam.mint.database;

import com.bindglam.mint.MintConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

public final class RedisDatabase implements Database<Jedis, JedisException> {
    private final MintConfiguration config;

    private JedisPool pool;

    public RedisDatabase(MintConfiguration config) {
        this.config = config;
    }

    @Override
    public void start() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        //poolConfig.setMaxTotal(128);
        //poolConfig.setMaxIdle(128);

        this.pool = new JedisPool(poolConfig, config.database.redis.host.value(), config.database.redis.port.value(),
                config.database.redis.timeout.value(), config.database.redis.password.value());
    }

    @Override
    public void stop() {
        this.pool.close();
    }

    @Override
    public void getResource(ResourceConsumer<Jedis, JedisException> consumer) {
        try(Jedis resource = pool.getResource()) {
            consumer.accept(resource);
        } catch (JedisException e) {
            throw new RuntimeException("Failed to proceed redis database connection", e);
        }
    }
}
