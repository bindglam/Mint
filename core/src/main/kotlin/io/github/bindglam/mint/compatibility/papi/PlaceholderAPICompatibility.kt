package io.github.bindglam.mint.compatibility.papi

import io.github.bindglam.mint.compatibility.Compatibility
import io.github.bindglam.mint.compatibility.printCompatIssue
import io.github.bindglam.mint.manager.Context
import io.github.bindglam.mint.manager.DatabaseManagerImpl

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