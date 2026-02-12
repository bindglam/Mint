package com.bindglam.mint.account.log;

import com.bindglam.mint.account.operation.Operation;
import com.bindglam.mint.currency.Currency;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Log record
 *
 * @param timestamp
 * @param operation
 * @param currency
 * @param result
 * @param value
 */
public record TransactionLog(
        Timestamp timestamp,
        Operation operation,
        Currency currency,
        Operation.Result result,
        BigDecimal value
) {
}
