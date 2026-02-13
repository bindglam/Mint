package com.bindglam.mint.manager

import com.bindglam.mint.database.Database
import redis.clients.jedis.Jedis
import java.sql.Connection

object DatabaseManagerImpl : DatabaseManager {
    private lateinit var sqlDatabase: Database<Connection>
    private var redisDatabase: Database<Jedis>? = null

    override fun start(context: Context) {
        this.sqlDatabase = context.config().database.sql.type.value().create(context.config())

        this.sqlDatabase.start()
    }

    override fun end(context: Context) {
        this.sqlDatabase.stop()
    }

    override fun sql(): Database<Connection> = sqlDatabase
    override fun redis(): Database<Jedis>? = redisDatabase
}