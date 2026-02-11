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
    DEPOSIT(BigDecimal::add,
            (table) -> "UPDATE " + table + " SET balance = balance + ? WHERE holder = ? AND currency = ?",
            (statement, holder, currency, value) -> {
                statement.setBigDecimal(1, value);
                statement.setString(2, holder.toString());
                statement.setString(3, currency.id());
            }),
    WITHDRAW((a, b) -> a.compareTo(b) > 0 ? a.subtract(b) : a,
            (table) -> "UPDATE " + table + " SET balance = balance - ? WHERE holder = ? AND currency = ? AND balance >= ?",
            (statement, holder, currency, value) -> {
                statement.setBigDecimal(1, value);
                statement.setString(2, holder.toString());
                statement.setString(3, currency.id());
                statement.setBigDecimal(4, value);
            });

    private final BiFunction<BigDecimal, BigDecimal, BigDecimal> binaryOperator;
    private final QuerySupplier query;
    private final StatementApplier applier;

    Operation(BiFunction<BigDecimal, BigDecimal, BigDecimal> binaryOperator, QuerySupplier query, StatementApplier applier) {
        this.binaryOperator = binaryOperator;
        this.query = query;
        this.applier = applier;
    }

    public BigDecimal operateBinary(BigDecimal a, BigDecimal b) {
        return binaryOperator.apply(a, b);
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
