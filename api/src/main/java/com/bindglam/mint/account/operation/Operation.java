package com.bindglam.mint.account.operation;

import java.math.BigDecimal;
import java.util.function.BiFunction;

/**
 * Represents the type of balance modification operation that can be performed on an account.
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

    /**
     * Applies this operation to the given balance and value.
     *
     * @param a The current balance
     * @param b The value to apply
     * @return The result of the operation
     */
    public Result operate(BigDecimal a, BigDecimal b) {
        return operator.apply(a, b);
    }

    /**
     * Represents the result of an operation attempt.
     *
     * @param success Whether the operation succeeded
     * @param result The new balance after the operation attempt
     */
    public record Result(boolean success, BigDecimal result) {
        /**
         * Returns whether the operation was successful.
         *
         * @return true if the operation succeeded, false otherwise
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * Creates a successful result with the given balance.
         *
         * @param result The new balance
         * @return A successful Result
         */
        public static Result success(BigDecimal result) {
            return new Result(true, result);
        }

        /**
         * Creates a failed result with the original balance unchanged.
         *
         * @param result The original balance
         * @return A failed Result
         */
        public static Result failure(BigDecimal result) {
            return new Result(false, result);
        }
    }
}
