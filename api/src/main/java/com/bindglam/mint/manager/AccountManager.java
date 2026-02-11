package com.bindglam.mint.manager;

import com.bindglam.mint.account.Account;
import com.bindglam.mint.account.CachedAccount;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

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
    @NotNull Account getAccount(UUID uuid);

    @Nullable CachedAccount getCachedAccount(UUID uuid);
}
