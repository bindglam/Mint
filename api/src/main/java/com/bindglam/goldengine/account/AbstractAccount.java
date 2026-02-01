package com.bindglam.goldengine.account;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bindglam.goldengine.GoldEngine;
import com.bindglam.goldengine.currency.Currency;
import com.bindglam.goldengine.manager.AccountManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * An abstract implementation of Account interface
 *
 * @author bindglam
 */
public abstract class AbstractAccount implements Account {
    private final UUID holder;

    private final Map<String, BigDecimal> balance = new HashMap<>();
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
                    JSONObject json = JSON.parseObject(result.getString("balance"));
                    for (Currency currency : GoldEngine.instance().currencyManager().registry().entries()) {
                        if(json.containsKey(currency.id())) {
                            this.balance.put(currency.id(), json.getBigDecimal(currency.id()));
                        } else {
                            this.balance.put(currency.id(), BigDecimal.ZERO);
                        }
                    }

                    this.isJustCreated = false;
                } else {
                    for (Currency currency : GoldEngine.instance().currencyManager().registry().entries()) {
                        this.balance.put(currency.id(), BigDecimal.ZERO);
                    }

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

                    JSONObject json = new JSONObject();
                    GoldEngine.instance().currencyManager().registry().entries().forEach(currency ->
                            json.put(currency.id(), this.balance(currency)));
                    statement.setString(2, json.toString());

                    statement.executeUpdate();
                }
            } else {
                try (PreparedStatement statement = connection.prepareStatement("UPDATE " + AccountManager.ACCOUNTS_TABLE_NAME + " SET balance = ? WHERE holder = ?")) {
                    JSONObject json = new JSONObject();
                    GoldEngine.instance().currencyManager().registry().entries().forEach(currency ->
                            json.put(currency.id(), this.balance(currency)));
                    statement.setString(1, json.toString());

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
    public BigDecimal balance(Currency currency) {
        return this.balance.getOrDefault(currency.id(), BigDecimal.ZERO);
    }

    @Override
    public void balance(Currency currency, BigDecimal balance) {
        this.balance.put(currency.id(), balance);
    }

    @Override
    public boolean modifyBalance(Currency currency, BigDecimal amount, Operation operation) {
        Operation.Result result = operation.operate(this.balance(currency), amount);
        if(result.isFailed())
            return false;
        this.balance(currency, result.result());
        return true;
    }

    @Override
    public void close() {
        save();
    }
}
