package io.github.bindglam.mint.account.log;

import lombok.Builder;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Manages transaction logs for an account, providing retrieval and clearing capabilities.
 */
public interface TransactionLogger {
    /**
     * Retrieves transaction logs with pagination support.
     *
     * @param pagination The pagination parameters specifying page and size
     * @return A CompletableFuture containing the list of transaction logs
     */
    @Unmodifiable
    CompletableFuture<List<TransactionLog>> retrieveLogs(Pagination pagination);

    /**
     * Clears all transaction logs for this account.
     */
    CompletableFuture<Void> clear();

    /**
     * Defines pagination parameters for log retrieval.
     *
     * @param page The zero-based page number
     * @param size The number of entries per page
     */
    @Builder
    record Pagination(int page, int size) {
    }
}
