package com.bindglam.goldengine.account;

import java.util.UUID;

/**
 * An implementation of AbstractAccount class for online accounts
 *
 * @author bindglam
 */
public class OnlineAccount extends AbstractAccount {
    public OnlineAccount(UUID holder) {
        super(holder);
    }

    @Override
    public void close() {
        // 아무것도 안하기(여기서 세이브하면 비효율적임)
    }
}
