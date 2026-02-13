package com.bindglam.mint.database;

import com.bindglam.mint.MintConfiguration;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.function.Function;

/**
 * Database type enum
 *
 * @author bindglam
 */
public enum SQLDatabaseType {
    SQLITE(SQLiteDatabase::new),
    MYSQL(MySQLDatabase::new);

    private final Function<MintConfiguration, Database<Connection>> supplier;

    SQLDatabaseType(Function<MintConfiguration, Database<Connection>> supplier) {
        this.supplier = supplier;
    }

    public @NotNull Database<Connection> create(MintConfiguration config) {
        return supplier.apply(config);
    }
}
