package com.bindglam.goldengine;

import com.bindglam.goldengine.database.Database;
import com.bindglam.goldengine.manager.AccountManager;
import com.bindglam.goldengine.manager.CurrencyManager;

public interface GoldEnginePlugin {
    void reload();

    GoldEngineConfiguration config();

    Database database();

    AccountManager accountManager();

    CurrencyManager currencyManager();
}
