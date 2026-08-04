package io.github.bindglam.mint.events;

import io.github.bindglam.mint.account.Account;
import io.github.bindglam.mint.account.operation.Operation;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Event fired when a balance modification operation is performed on an account.
 */
public class AccountOperationEvent extends AccountEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Operation operation;
    private final BigDecimal amount;
    private final Operation.Result result;

    /**
     * Internal constructor for creating AccountOperationEvent instances.
     *
     * @param account   The account associated with this event
     * @param operation The type of operation performed
     * @param amount    The amount that was applied
     * @param result    The result of the operation
     */
    @ApiStatus.Internal
    public AccountOperationEvent(Account account, Operation operation, BigDecimal amount, Operation.Result result) {
        super(account);
        this.operation = operation;
        this.amount = amount;
        this.result = result;
    }

    /**
     * Returns the type of operation that was performed.
     *
     * @return The operation type
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * Returns the amount that was applied in the operation.
     *
     * @return The amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Returns the result of the operation.
     *
     * @return The operation result
     */
    public Operation.Result getResult() {
        return result;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * Returns the HandlerList for this event.
     *
     * @return The HandlerList
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
