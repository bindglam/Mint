package com.bindglam.mint.database;

import com.bindglam.mint.MintConfiguration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * An implementation of Database interface for MySQL
 *
 * @author bindglam
 */
public final class MySQLDatabase implements Database<Connection, SQLException> {
    private HikariDataSource dataSource;

    private final MintConfiguration config;

    public MySQLDatabase(MintConfiguration config) {
        this.config = config;
    }

    @Override
    public void start() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + config.database.sql.mysql.url.value() + "/" + config.database.sql.mysql.database.value());
        hikariConfig.setUsername(config.database.sql.mysql.username.value());
        hikariConfig.setPassword(config.database.sql.mysql.password.value());
        hikariConfig.setMaximumPoolSize(config.database.sql.mysql.maxPoolSize.value());

        this.dataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public void stop() {
        this.dataSource.close();
    }

    @Override
    public void getResource(ResourceConsumer<Connection, SQLException> consumer) {
        try(Connection connection = dataSource.getConnection()) {
            consumer.accept(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to proceed database connection", e);
        }
    }
}
