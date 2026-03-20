package com.bindglam.mint.account.log;

import lombok.Builder;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * TransactionLogger interface
 */
public interface TransactionLogger {
    /**
     * Retrieve logs
     *
     * @param pagination pagination
     * @return logs
     */
    @Unmodifiable
    CompletableFuture<List<TransactionLog>> retrieveLogs(Pagination pagination);

    void clear();

    @Builder
    record Pagination(int page, int size) {
    }
}
