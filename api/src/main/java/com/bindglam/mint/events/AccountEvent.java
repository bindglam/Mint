package com.bindglam.mint.events;

import com.bindglam.mint.account.Account;
import org.bukkit.event.Event;

/**
 * Base event class for all account-related events.
 * Events are asynchronous by default.
 */
public abstract class AccountEvent extends Event {
    private final Account account;

    /**
     * Constructs a new AccountEvent for the specified account.
     *
     * @param account The account associated with this event
     */
    protected AccountEvent(Account account) {
        super(true);
        this.account = account;
    }

    /**
     * Returns the account associated with this event.
     *
     * @return The account
     */
    public Account getAccount() {
        return account;
    }
}
