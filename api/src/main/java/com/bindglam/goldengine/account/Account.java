package com.bindglam.goldengine.account;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Account extends AutoCloseable {
    @NotNull UUID holder();

    double balance();

    void balance(double amount);
}
