package com.bindglam.mint;

import com.bindglam.mint.manager.AccountManager;
import com.bindglam.mint.manager.CurrencyManager;
import com.bindglam.mint.manager.DatabaseManager;

/**
 * GoldEnginePlugin interface
 *
 * @author bindglam
 */
public interface MintPlugin {
    void reload();

    MintConfiguration config();

    DatabaseManager databaseManager();

    AccountManager accountManager();

    CurrencyManager currencyManager();
}
