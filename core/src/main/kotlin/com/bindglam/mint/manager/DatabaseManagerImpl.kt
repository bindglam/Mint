package com.bindglam.mint.manager

import com.bindglam.mint.database.Database
import redis.clients.jedis.Jedis
import redis.clients.jedis.exceptions.JedisException
import java.sql.Connection
import java.sql.SQLException

object DatabaseManagerImpl : DatabaseManager {
    private lateinit var sqlDatabase: Database<Connection, SQLException>
    private var redisDatabase: Database<Jedis, JedisException>? = null

    override fun start(context: Context) {
        this.sqlDatabase = context.config().database.sql.type.value().create(context.config())

        this.sqlDatabase.start()
    }

    override fun end(context: Context) {
        this.sqlDatabase.stop()
    }

    override fun sql(): Database<Connection, SQLException> = sqlDatabase
    override fun redis(): Database<Jedis, JedisException>? = redisDatabase
}