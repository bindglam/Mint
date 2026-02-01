package com.bindglam.goldengine.account;

import java.util.UUID;

/**
 * An implementation of AbstractAccount class for offline accounts
 *
 * @author bindglam
 */
public class OfflineAccount extends AbstractAccount {
    public OfflineAccount(UUID holder) {
        super(holder);
    }
}
