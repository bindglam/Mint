package com.bindglam.mint;

import com.bindglam.config.Configuration;
import com.bindglam.config.Field;
import com.bindglam.config.complex.EnumField;
import com.bindglam.mint.database.SQLDatabaseType;

import java.io.File;

public final class MintConfiguration extends Configuration {
    public final Field<String> language = createPrimitiveField("language", "english");

    public final Database database = new Database();
    public final class Database {
        public final SQL sql = new SQL();
        public final class SQL {
            public final Field<SQLDatabaseType> type = createExtendedComplexField(() -> new EnumField<>("database.sql.type", SQLDatabaseType.SQLITE, SQLDatabaseType.class));

            public final SQLite sqlite = new SQLite();
            public final class SQLite {
                public final Field<Boolean> autoCommit = createPrimitiveField("database.sql.SQLITE.auto-commit", true);
                public final Field<Integer> validTimeout = createPrimitiveField("database.sql.SQLITE.valid-timeout", 500);
            }

            public final MySQL mysql = new MySQL();
            public final class MySQL {
                public final Field<String> url = createPrimitiveField("database.sql.MYSQL.url", "localhost:3306");
                public final Field<String> database = createPrimitiveField("database.sql.MYSQL.database", "minecraft");
                public final Field<String> username = createPrimitiveField("database.sql.MYSQL.username", "root");
                public final Field<String> password = createPrimitiveField("database.sql.MYSQL.password", "1234");
                public final Field<Integer> maxPoolSize = createPrimitiveField("database.sql.MYSQL.max-pool-size", 10);
            }
        }

        public final Redis redis = new Redis();
        public final class Redis {
            public final Field<Boolean> enabled = createPrimitiveField("database.redis.enabled", false);
            public final Field<String> host = createPrimitiveField("database.redis.host", "localhost");
            public final Field<Integer> port = createPrimitiveField("database.redis.port", 6379);
            public final Field<String> password = createPrimitiveField("database.redis.password", "1234");
            public final Field<Integer> timeout = createPrimitiveField("database.redis.timeout", 1000);
        }
    }

    public final Economy economy = new Economy();
    public final class Economy {
        public final Currency currency = new Currency();
        public final class Currency {
            public final Field<String> defaultCurrency = createPrimitiveField("economy.currency.default-currency", "won");
        }
    }

    public MintConfiguration(File file) {
        super(file);
    }
}
