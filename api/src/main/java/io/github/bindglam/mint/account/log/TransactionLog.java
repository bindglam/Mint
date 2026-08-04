package io.github.bindglam.mint.account.log;

import io.github.bindglam.mint.account.operation.Operation;
import io.github.bindglam.mint.currency.Currency;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Represents a single transaction log entry recording a balance modification.
 *
 * @param loggedAt The time when the transaction occurred
 * @param operation The type of operation performed (DEPOSIT or WITHDRAW)
 * @param currency The currency involved in the transaction
 * @param result   The result of the operation, including success status and new balance
 * @param value    The original value that was applied in the operation
 */
public record TransactionLog(
        Timestamp loggedAt,
        Operation operation,
        Currency currency,
        Operation.Result result,
        BigDecimal value
) {
}
