package com.bindglam.mint.compatibility.papi

import com.bindglam.mint.compatibility.Compatibility

object PlaceholderAPICompatibility : Compatibility {
    override val requiredPlugin: String = "PlaceholderAPI"

    override fun start() {
        MintExpansion.register()
    }

    override fun end() {
    }
}