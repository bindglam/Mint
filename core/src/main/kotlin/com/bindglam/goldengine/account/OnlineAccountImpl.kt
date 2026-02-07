package com.bindglam.goldengine.account

import java.util.UUID

class OnlineAccountImpl(holder: UUID) : AbstractAccount(holder), OnlineAccount {
    override fun close() {
        // 아무것도 안하기(여기서 세이브하면 비효율적임)
    }
}
