package com.bindglam.goldengine.account;

import java.math.BigDecimal;
import java.util.function.BiFunction;

/**
 * Operation enum
 *
 * @author bindglam
 */
public enum Operation {
    ADD((a, b) -> Result.success(a.add(b))),
    SUBTRACT((a, b) -> {
        if(a.compareTo(b) < 0)
            return Result.failure();

        return Result.success(a.subtract(b));
    });

    private final BiFunction<BigDecimal, BigDecimal, Result> function;

    Operation(BiFunction<BigDecimal, BigDecimal, Result> function) {
        this.function = function;
    }

    public Result operate(BigDecimal a, BigDecimal b) {
        return function.apply(a, b);
    }

    public record Result(boolean success, BigDecimal result) {
        public boolean isSuccess() {
            return success;
        }

        public boolean isFailed() {
            return !success;
        }

        public static Result success(BigDecimal result) {
            return new Result(true, result);
        }

        public static Result failure() {
            return new Result(false, null);
        }
    }
}
