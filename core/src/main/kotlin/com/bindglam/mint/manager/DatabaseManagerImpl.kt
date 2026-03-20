package com.bindglam.mint.manager

import io.github.bindglam.database.Database
import io.github.bindglam.database.MySQLDatabase
import io.github.bindglam.database.RedisDatabase
import io.github.bindglam.database.SQLiteDatabase
import com.bindglam.mint.MintConfiguration
import com.bindglam.mint.utils.dataFolder
import redis.clients.jedis.Jedis
import redis.clients.jedis.exceptions.JedisException
import java.io.File
import java.sql.Connection
import java.sql.SQLException

object DatabaseManagerImpl : Managerial {
    private lateinit var sqlDatabase: Database<Connection, SQLException>
    private var redisDatabase: Database<Jedis, JedisException>? = null

    override fun priority() = Managerial.Priority.of(Int.MAX_VALUE, Int.MIN_VALUE)

    override fun start(context: Context) {
        this.sqlDatabase = context.config().database.sql.type.value().supplier(context.config())
        this.redisDatabase = if(context.config().database.redis.enabled.value())
            RedisDatabase(context.config().database.redis.host.value(), context.config().database.redis.port.value(), context.config().database.redis.timeout.value(), context.config().database.redis.password.value(), 10)
        else null

        this.sqlDatabase.start()
        this.redisDatabase?.start()
    }

    override fun end(context: Context) {
        this.sqlDatabase.stop()
        this.redisDatabase?.stop()
    }

    fun sql(): Database<Connection, SQLException> = sqlDatabase
    fun redis(): Database<Jedis, JedisException>? = redisDatabase

    enum class SQLDatabaseType(val supplier: (MintConfiguration) -> Database<Connection, SQLException>) {
        SQLITE({ config -> SQLiteDatabase(File(dataFolder(), "database.db"), config.database.sql.sqlite.autoCommit.value(), config.database.sql.sqlite.validTimeout.value()) }),
        MYSQL({ config -> MySQLDatabase(config.database.sql.mysql.url.value(), config.database.sql.mysql.database.value(), config.database.sql.mysql.username.value(), config.database.sql.mysql.password.value(), config.database.sql.mysql.maxPoolSize.value()) });
    }
}