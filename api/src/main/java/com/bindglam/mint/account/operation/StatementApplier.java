package com.bindglam.mint.account.operation;

import com.bindglam.mint.currency.Currency;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@FunctionalInterface
public interface StatementApplier {
    void apply(PreparedStatement statement, UUID holder, Currency currency, BigDecimal value) throws SQLException;
}
