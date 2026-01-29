package com.bindglam.goldengine.database;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public enum DatabaseType {
    SQLITE(SQLiteDatabase::new),
    MYSQL(SQLiteDatabase::new);

    private final Function<ConfigurationSection, Database> supplier;

    DatabaseType(Function<ConfigurationSection, Database> supplier) {
        this.supplier = supplier;
    }

    public @NotNull Database create(ConfigurationSection config) {
        return supplier.apply(config.getConfigurationSection(this.name()));
    }
}
