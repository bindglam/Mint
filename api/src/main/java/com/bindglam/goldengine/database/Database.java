package com.bindglam.goldengine.database;

public interface Database {
    void start();

    void stop();

    void getConnection(ConnectionConsumer consumer);
}
