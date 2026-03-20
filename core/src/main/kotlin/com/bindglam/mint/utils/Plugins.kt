package com.bindglam.mint.utils

import com.bindglam.mint.Mint
import com.bindglam.mint.MintPlugin
import com.bindglam.mint.MintPluginImpl
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import java.io.File

fun logger(): Logger = Mint.instance().plugin().slF4JLogger

fun dataFolder(): File = Mint.instance().plugin().dataFolder

fun MintPlugin.plugin(): MintPluginImpl = this as MintPluginImpl