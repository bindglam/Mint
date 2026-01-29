package com.bindglam.goldengine.manager;

import com.bindglam.goldengine.account.Account;
import com.bindglam.goldengine.account.OfflineAccount;
import com.bindglam.goldengine.account.OnlineAccount;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AccountManager extends Managerial {
    String ACCOUNTS_TABLE_NAME = "goldengine_accounts";

    CompletableFuture<? extends Account> getAccount(UUID uuid);

    @Nullable OnlineAccount getOnlineAccount(UUID uuid);

    CompletableFuture<@NotNull OfflineAccount> getOfflineAccount(UUID uuid);
}
