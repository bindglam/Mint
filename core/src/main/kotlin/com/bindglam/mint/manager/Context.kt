package com.bindglam.mint.manager

import com.bindglam.mint.MintConfiguration
import com.bindglam.mint.MintPlugin
import com.bindglam.mint.utils.plugin

class Context(private val plugin: MintPlugin) {
    fun plugin() = this.plugin
    fun config(): MintConfiguration = this.plugin.plugin().mintConfig
    fun logger() = this.plugin.plugin().logger
}