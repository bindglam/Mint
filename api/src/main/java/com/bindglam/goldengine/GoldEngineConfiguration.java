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

        public final MySQL mysql = new MySQL();
        public final class MySQL {
            public final Field<String> url = createPrimitiveField("database.MYSQL.url", "localhost:3306");
            public final Field<String> database = createPrimitiveField("database.MYSQL.database", "minecraft");
            public final Field<String> username = createPrimitiveField("database.MYSQL.username", "root");
            public final Field<String> password = createPrimitiveField("database.MYSQL.password", "1234");
            public final Field<Integer> maxPoolSize = createPrimitiveField("database.MYSQL.max-pool-size", 10);
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
