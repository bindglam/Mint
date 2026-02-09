package com.bindglam.mint.account.operation;

import com.bindglam.mint.currency.Currency;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * Operation enum
 *
 * @author bindglam
 */
public enum Operation {
    DEPOSIT((table) -> "UPDATE " + table + " SET balance = balance + ? WHERE holder = ? AND currency = ?",
            (statement, holder, currency, value) -> {
                statement.setBigDecimal(1, value);
                statement.setString(2, holder.toString());
                statement.setString(3, currency.id());
            }),
    WITHDRAW((table) -> "UPDATE " + table + " SET balance = balance - ? WHERE holder = ? AND currency = ? AND balance >= ?",
            (statement, holder, currency, value) -> {
                statement.setBigDecimal(1, value);
                statement.setString(2, holder.toString());
                statement.setString(3, currency.id());
                statement.setBigDecimal(4, value);
            });

    private final QuerySupplier query;
    private final StatementApplier applier;

    Operation(QuerySupplier query, StatementApplier applier) {
        this.query = query;
        this.applier = applier;
    }

    public String getQuery(String table) {
        return query.supply(table);
    }

    public void applyStatement(PreparedStatement statement, UUID holder, Currency currency, BigDecimal value) throws SQLException {
        applier.apply(statement, holder, currency, value);
    }

    public record Result(boolean success, BigDecimal result) {
        public boolean isSuccess() {
            return success;
        }

        public static Result success(BigDecimal result) {
            return new Result(true, result);
        }

        public static Result failure(BigDecimal result) {
            return new Result(false, result);
        }
    }
}
