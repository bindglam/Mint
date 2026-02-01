package com.bindglam.goldengine;

import com.bindglam.goldengine.database.Database;
import com.bindglam.goldengine.manager.AccountManager;
import com.bindglam.goldengine.manager.CurrencyManager;

/**
 * GoldEnginePlugin interface
 *
 * @author bindglam
 */
public interface GoldEnginePlugin {
    void reload();

    GoldEngineConfiguration config();

    Database database();

    AccountManager accountManager();

    CurrencyManager currencyManager();
}
