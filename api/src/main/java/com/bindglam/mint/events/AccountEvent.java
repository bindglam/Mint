package com.bindglam.mint.events;

import com.bindglam.mint.account.Account;
import org.bukkit.event.Event;

public abstract class AccountEvent extends Event {
    private final Account account;

    protected AccountEvent(Account account) {
        super(true);
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }
}
