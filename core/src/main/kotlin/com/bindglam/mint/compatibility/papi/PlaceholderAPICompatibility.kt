package com.bindglam.mint.compatibility.papi

import com.bindglam.mint.compatibility.Compatibility
import com.bindglam.mint.compatibility.printCompatIssue
import com.bindglam.mint.manager.Context
import com.bindglam.mint.manager.DatabaseManagerImpl

object PlaceholderAPICompatibility : Compatibility {
    override val requiredPlugin: String = "PlaceholderAPI"

    override fun start(context: Context) {
        MintExpansion.register()

        if(context.config().database.sql.type.value() == DatabaseManagerImpl.SQLDatabaseType.MYSQL || context.config().database.redis.enabled.value()) {
            printCompatIssue()
        }
    }

    override fun end(context: Context) {
    }
}