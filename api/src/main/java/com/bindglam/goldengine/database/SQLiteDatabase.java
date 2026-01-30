package com.bindglam.goldengine.database;

import com.bindglam.goldengine.GoldEngineConfiguration;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class SQLiteDatabase implements Database {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteDatabase.class);

    private final GoldEngineConfiguration config;

    private @Nullable Connection connection;

    public SQLiteDatabase(GoldEngineConfiguration config) {
        this.config = config;
    }

    @Override
    public void start() {
        LOGGER.info("Try connecting to database...");

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        connect();

        LOGGER.info("Successfully connected to database");
    }

    @Override
    public void stop() {
        LOGGER.info("Try stopping database...");

        disconnect();

        LOGGER.info("Successfully stopped database");
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/GoldEngine/database.db");
            connection.setAutoCommit(config.database.sqlite.autoCommit.value());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    private void disconnect() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to disconnect to database", e);
        }
    }

    private Connection ensureConnection() {
        try {
            if(this.connection == null || this.connection.isValid(config.database.sqlite.validTimeout.value())) {
                this.disconnect();
                this.connect();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to ensure connection to database", e);
        }

        return this.connection;
    }

    @Override
    public void getConnection(ConnectionConsumer consumer) {
        try {
            consumer.accept(ensureConnection());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to proceed database connection", e);
        }
    }
}
