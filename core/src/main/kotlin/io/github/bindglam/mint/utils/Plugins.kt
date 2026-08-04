package io.github.bindglam.mint.utils

import io.github.bindglam.mint.Mint
import io.github.bindglam.mint.MintPlugin
import io.github.bindglam.mint.MintPluginImpl
import org.slf4j.Logger
import java.io.File

fun logger(): Logger = Mint.instance().plugin().slF4JLogger

fun dataFolder(): File = Mint.instance().plugin().dataFolder

fun MintPlugin.plugin(): MintPluginImpl = this as MintPluginImpl