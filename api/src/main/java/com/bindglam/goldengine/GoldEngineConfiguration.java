package com.bindglam.goldengine;

import com.bindglam.config.Configuration;
import com.bindglam.config.Field;
import com.bindglam.config.complex.EnumField;
import com.bindglam.goldengine.database.DatabaseType;

import java.io.File;

public final class GoldEngineConfiguration extends Configuration {
    public final Field<String> language = createPrimitiveField("language", "korean");

    public final Database database = new Database();
    public final class Database {
        public final Field<DatabaseType> type = createExtendedComplexField(() -> new EnumField<>("database.type", DatabaseType.SQLITE, DatabaseType.class));

        public final SQLite sqlite = new SQLite();
        public final class SQLite {
            public final Field<Boolean> autoCommit = createPrimitiveField("database.SQLITE.auto-commit", true);
            public final Field<Integer> validTimeout = createPrimitiveField("database.SQLITE.valid-timeout", 500);
        }
    }

    public final Features features = new Features();
    public final class Features {
        public final Boast boast = new Boast();
        public final class Boast {
            public final Field<Boolean> enabled = createPrimitiveField("features.boast.enabled", true);
            public final Field<Double> cost = createPrimitiveField("features.boast.cost", 50000.0);
        }
    }

    public GoldEngineConfiguration(File file) {
        super(file);
    }
}
