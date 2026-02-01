package com.bindglam.goldengine.database;

import com.bindglam.goldengine.GoldEngineConfiguration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class MySQLDatabase implements Database {
    private HikariDataSource dataSource;

    private final GoldEngineConfiguration config;

    public MySQLDatabase(GoldEngineConfiguration config) {
        this.config = config;
    }

    @Override
    public void start() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + config.database.mysql.url.value() + "/" + config.database.mysql.database.value());
        hikariConfig.setUsername(config.database.mysql.username.value());
        hikariConfig.setPassword(config.database.mysql.password.value());
        hikariConfig.setMaximumPoolSize(config.database.mysql.maxPoolSize.value());

        this.dataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public void stop() {
        this.dataSource.close();
    }

    @Override
    public void getConnection(ConnectionConsumer consumer) {
        try(Connection connection = dataSource.getConnection()) {
            consumer.accept(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to proceed database connection", e);
        }
    }
}
