package com.bindglam.goldengine.account;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public interface Account extends AutoCloseable {
    @NotNull UUID holder();

    BigDecimal balance();

    void balance(BigDecimal balance);

    boolean modifyBalance(BigDecimal amount, Operation operation);
}
