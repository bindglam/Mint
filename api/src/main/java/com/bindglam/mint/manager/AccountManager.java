package com.bindglam.mint.manager;

import com.bindglam.mint.account.Account;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Provides access to player accounts for economic operations.
 *
 * @author bindglam
 */
public interface AccountManager {
    /**
     * Retrieves the account associated with the specified player UUID.
     *
     * @param uuid The unique identifier of the player
     * @return The account for the given UUID
     */
    @NotNull Account getAccount(UUID uuid);
}
