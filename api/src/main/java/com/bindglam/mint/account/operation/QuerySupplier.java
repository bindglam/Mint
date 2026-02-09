package com.bindglam.mint.account.operation;

@FunctionalInterface
public interface QuerySupplier {
    String supply(String table);
}
