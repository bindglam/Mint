package com.bindglam.mint.manager

import com.bindglam.mint.database.Database
import com.bindglam.mint.database.RedisDatabase
import redis.clients.jedis.Jedis
import redis.clients.jedis.exceptions.JedisException
import java.sql.Connection
import java.sql.SQLException

object DatabaseManagerImpl : DatabaseManager {
    private lateinit var sqlDatabase: Database<Connection, SQLException>
    private var redisDatabase: Database<Jedis, JedisException>? = null

    override fun priority() = Managerial.Priority.of(Int.MAX_VALUE, Int.MIN_VALUE)

    override fun start(context: Context) {
        this.sqlDatabase = context.config().database.sql.type.value().create(context.config())
        this.redisDatabase = if(context.config().database.redis.enabled.value())
            RedisDatabase(context.config())
        else null

        this.sqlDatabase.start()
        this.redisDatabase?.start()
    }

    override fun end(context: Context) {
        this.sqlDatabase.stop()
        this.redisDatabase?.stop()
    }

    override fun sql(): Database<Connection, SQLException> = sqlDatabase
    override fun redis(): Database<Jedis, JedisException>? = redisDatabase
}