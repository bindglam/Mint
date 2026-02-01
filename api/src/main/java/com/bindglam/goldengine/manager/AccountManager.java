package com.bindglam.goldengine.manager;

import com.bindglam.goldengine.account.Account;
import com.bindglam.goldengine.account.OfflineAccount;
import com.bindglam.goldengine.account.OnlineAccount;
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
    String ACCOUNTS_TABLE_NAME = "goldengine_accounts";

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
