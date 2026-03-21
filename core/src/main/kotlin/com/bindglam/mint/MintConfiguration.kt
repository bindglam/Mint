package com.bindglam.mint

import com.bindglam.config.Configuration
import com.bindglam.config.complex.EnumField
import com.bindglam.mint.manager.DatabaseManagerImpl
import java.io.File
import java.util.function.Supplier

class MintConfiguration(file: File) : Configuration(file) {
    val language = createPrimitiveField<String>("language", "english")!!

    val database = Database()
    inner class Database {
        val sql = SQL()
        inner class SQL {
            val type = createExtendedComplexField(Supplier { EnumField("database.sql.type", DatabaseManagerImpl.SQLDatabaseType.SQLITE, DatabaseManagerImpl.SQLDatabaseType::class.java) })!!

            val autoCommit = createPrimitiveField("database.sql.SQLITE.auto-commit", true)!!
            val validTimeout = createPrimitiveField("database.sql.SQLITE.valid-timeout", 500)!!

            val mysql = MySQL()
            inner class MySQL {
                val url = createPrimitiveField("database.sql.MYSQL.url", "localhost:3306")!!
                val database = createPrimitiveField("database.sql.MYSQL.database", "minecraft")!!
                val username = createPrimitiveField("database.sql.MYSQL.username", "root")!!
                val password = createPrimitiveField("database.sql.MYSQL.password", "1234")!!
                val maxPoolSize = createPrimitiveField("database.sql.MYSQL.max-pool-size", 10)!!
            }
        }

        val redis = Redis()
        inner class Redis {
            val enabled = createPrimitiveField("database.redis.enabled", false)!!
            val host = createPrimitiveField("database.redis.host", "localhost")!!
            val port = createPrimitiveField("database.redis.port", 6379)!!
            val password = createPrimitiveField("database.redis.password", "1234")!!
            val timeout = createPrimitiveField("database.redis.timeout", 1000)!!
            val syncInterval = createPrimitiveField("database.redis.sync-interval", 60)!!
        }
    }

    val economy = Economy()
    inner class Economy {
        val currency = Currency()
        inner class Currency {
            val defaultCurrency = createPrimitiveField("economy.currency.default-currency", "won")!!
        }
    }
}