package com.bindglam.mint;

import com.bindglam.mint.manager.AccountManager;
import com.bindglam.mint.manager.CurrencyManager;

/**
 * Main plugin interface that provides access to Mint's core managers.
 *
 * @author bindglam
 */
public interface MintPlugin {
    /**
     * Reloads the plugin configuration and refreshes all managers.
     */
    void reload();

    /**
     * Returns the account manager for accessing player accounts.
     *
     * @return The account manager
     */
    AccountManager accountManager();

    /**
     * Returns the currency manager for accessing currency definitions.
     *
     * @return The currency manager
     */
    CurrencyManager currencyManager();
}
