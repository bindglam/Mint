package io.github.bindglam.mint.compatibility

import io.github.bindglam.mint.manager.Context

interface Compatibility {
    val requiredPlugin: String

    fun start(context: Context)

    fun end(context: Context)
}