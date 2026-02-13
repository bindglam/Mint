package com.bindglam.mint.events;

import com.bindglam.mint.account.Account;
import com.bindglam.mint.account.operation.Operation;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class AccountOperationEvent extends AccountEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Operation operation;
    private final BigDecimal amount;
    private final Operation.Result result;

    @ApiStatus.Internal
    public AccountOperationEvent(Account account, Operation operation, BigDecimal amount, Operation.Result result) {
        super(account);
        this.operation = operation;
        this.amount = amount;
        this.result = result;
    }

    public Operation getOperation() {
        return operation;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Operation.Result getResult() {
        return result;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
