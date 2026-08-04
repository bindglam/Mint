package io.github.bindglam.mint.manager

import io.github.bindglam.mint.MintConfiguration
import io.github.bindglam.mint.MintPlugin
import io.github.bindglam.mint.utils.plugin

class Context(private val plugin: MintPlugin) {
    fun plugin() = this.plugin
    fun config(): MintConfiguration = this.plugin.plugin().mintConfig
    fun logger() = this.plugin.plugin().logger
}