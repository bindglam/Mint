package com.bindglam.mint.account.operation;

import java.math.BigDecimal;
import java.util.function.BiFunction;

/**
 * Operation enum
 *
 * @author bindglam
 */
public enum Operation {
    DEPOSIT((a, b) -> Result.success(a.add(b))),
    WITHDRAW((a, b) -> a.compareTo(b) > 0 ? Result.success(a.subtract(b)) : Result.failure(a));

    private final BiFunction<BigDecimal, BigDecimal, Result> operator;

    Operation(BiFunction<BigDecimal, BigDecimal, Result> operator) {
        this.operator = operator;
    }

    public Result operate(BigDecimal a, BigDecimal b) {
        return operator.apply(a, b);
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
