package com.bindglam.mint.manager;

import com.bindglam.mint.account.Account;
import com.bindglam.mint.account.OfflineAccount;
import com.bindglam.mint.account.OnlineAccount;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * AccountManager interface
 *
 * @author bindglam
 */
public interface AccountManager extends Managerial {
    /**
     * Get account by uuid
     *
     * @param uuid uuid of the account
     */
    CompletableFuture<? extends Account> getAccount(UUID uuid);

    /**
     * Get online account by uuid
     *
     * @param uuid uuid of the online account
     */
    @Nullable OnlineAccount getOnlineAccount(UUID uuid);

    /**
     * Get offline account by uuid
     *
     * @param uuid uuid of the offline account
     */
    CompletableFuture<@NotNull OfflineAccount> getOfflineAccount(UUID uuid);
}
