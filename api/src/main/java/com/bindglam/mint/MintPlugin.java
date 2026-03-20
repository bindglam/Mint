package com.bindglam.mint;

import com.bindglam.mint.manager.AccountManager;
import com.bindglam.mint.manager.CurrencyManager;

/**
 * GoldEnginePlugin interface
 *
 * @author bindglam
 */
public interface MintPlugin {
    void reload();

    AccountManager accountManager();

    CurrencyManager currencyManager();
}
