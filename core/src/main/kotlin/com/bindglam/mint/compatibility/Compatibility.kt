package com.bindglam.mint.compatibility

import com.bindglam.mint.manager.Context

interface Compatibility {
    val requiredPlugin: String

    fun start(context: Context)

    fun end(context: Context)
}