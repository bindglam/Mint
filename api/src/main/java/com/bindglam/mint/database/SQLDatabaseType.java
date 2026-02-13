package com.bindglam.mint.database;

import com.bindglam.mint.MintConfiguration;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * Database type enum
 *
 * @author bindglam
 */
public enum SQLDatabaseType {
    SQLITE(SQLiteDatabase::new),
    MYSQL(MySQLDatabase::new);

    private final Function<MintConfiguration, Database<Connection, SQLException>> supplier;

    SQLDatabaseType(Function<MintConfiguration, Database<Connection, SQLException>> supplier) {
        this.supplier = supplier;
    }

    public @NotNull Database<Connection, SQLException> create(MintConfiguration config) {
        return supplier.apply(config);
    }
}
