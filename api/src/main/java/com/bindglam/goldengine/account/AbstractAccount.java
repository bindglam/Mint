package com.bindglam.goldengine.account;

import com.bindglam.goldengine.GoldEngine;
import com.bindglam.goldengine.manager.AccountManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public abstract class AbstractAccount implements Account {
    private final UUID holder;

    private double balance;
    private boolean isJustCreated;

    public AbstractAccount(UUID holder) {
        this.holder = holder;

        load();
    }

    private void load() {
        GoldEngine.instance().database().getConnection((connection) -> {
            try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + AccountManager.ACCOUNTS_TABLE_NAME + " WHERE holder = ?")) {
                statement.setString(1, this.holder.toString());

                ResultSet result = statement.executeQuery();
                if(result.next()) {
                    this.balance = result.getDouble("balance");

                    this.isJustCreated = false;
                } else {
                    this.balance = 0.0;

                    this.isJustCreated = true;
                }
            }
        });
    }

    @ApiStatus.Internal
    public void save() {
        GoldEngine.instance().database().getConnection((connection) -> {
            if (this.isJustCreated) {
                try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + AccountManager.ACCOUNTS_TABLE_NAME + " (holder, balance) VALUES (?, ?)")) {
                    statement.setString(1, this.holder.toString());
                    statement.setDouble(2, this.balance);

                    statement.executeUpdate();
                }
            } else {
                try (PreparedStatement statement = connection.prepareStatement("UPDATE " + AccountManager.ACCOUNTS_TABLE_NAME + " SET balance = ? WHERE holder = ?")) {
                    statement.setDouble(1, this.balance);
                    statement.setString(2, this.holder.toString());

                    statement.executeUpdate();
                }
            }
        });
    }

    @Override
    public @NotNull UUID holder() {
        return this.holder;
    }

    @Override
    public double balance() {
        return this.balance;
    }

    @Override
    public void balance(double amount) {
        this.balance = amount;
    }

    @Override
    public void close() {
        save();
    }
}
