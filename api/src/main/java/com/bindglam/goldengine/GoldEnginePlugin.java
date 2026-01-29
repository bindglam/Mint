package com.bindglam.goldengine;

import com.bindglam.goldengine.database.Database;
import com.bindglam.goldengine.manager.AccountManager;

public interface GoldEnginePlugin {
    GoldEngineConfiguration config();

    Database database();

    AccountManager accountManager();
}
