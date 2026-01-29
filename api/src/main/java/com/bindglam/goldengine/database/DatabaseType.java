package com.bindglam.goldengine.database;

import com.bindglam.goldengine.GoldEngineConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public enum DatabaseType {
    SQLITE(SQLiteDatabase::new),
    MYSQL(SQLiteDatabase::new);

    private final Function<GoldEngineConfiguration, Database> supplier;

    DatabaseType(Function<GoldEngineConfiguration, Database> supplier) {
        this.supplier = supplier;
    }

    public @NotNull Database create(GoldEngineConfiguration config) {
        return supplier.apply(config);
    }
}
