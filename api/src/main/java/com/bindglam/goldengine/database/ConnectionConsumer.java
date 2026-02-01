package com.bindglam.goldengine.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * ConnectionConsumer interface
 *
 * @author bindglam
 */
@FunctionalInterface
public interface ConnectionConsumer {
    void accept(Connection connection) throws SQLException;
}
