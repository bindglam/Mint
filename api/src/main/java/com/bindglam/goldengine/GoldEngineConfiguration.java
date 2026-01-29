package com.bindglam.goldengine;

import com.bindglam.config.Configuration;
import com.bindglam.config.Field;
import com.bindglam.config.complex.EnumField;
import com.bindglam.goldengine.database.DatabaseType;

import java.io.File;

public final class GoldEngineConfiguration extends Configuration {
    public final Database database = new Database();
    public final class Database {
        public final Field<DatabaseType> type = createExtendedComplexField(() -> new EnumField<>("database.type", DatabaseType.SQLITE, DatabaseType.class));
    }

    public final Economy economy = new Economy();
    public final class Economy {
        public final Field<String> currencyName = createPrimitiveField("economy.currency-name", "원");
    }

    public GoldEngineConfiguration(File file) {
        super(file);
    }
}
