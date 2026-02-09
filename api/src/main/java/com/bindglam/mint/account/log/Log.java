package com.bindglam.mint.account.log;

import com.bindglam.mint.account.operation.Operation;
import com.bindglam.mint.currency.Currency;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record Log(
        Timestamp timestamp,
        Operation operation,
        Currency currency,
        Operation.Result result,
        BigDecimal value
) {
}
