package com.bindglam.mint.account.log;

import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TransactionLogger {
    @Unmodifiable
    CompletableFuture<List<Log>> retrieveLogs(@Range(from = 1L, to = 99L) int limit, int offset);
}
