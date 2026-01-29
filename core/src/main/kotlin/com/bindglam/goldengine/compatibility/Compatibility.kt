package com.bindglam.goldengine.compatibility

interface Compatibility {
    val requiredPlugin: String

    fun start()

    fun end()
}