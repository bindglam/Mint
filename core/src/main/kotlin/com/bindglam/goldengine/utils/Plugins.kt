package com.bindglam.goldengine.utils

import com.bindglam.goldengine.GoldEngine
import com.bindglam.goldengine.GoldEnginePlugin
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger

fun logger(): Logger = GoldEngine.instance().plugin().slF4JLogger

fun GoldEnginePlugin.plugin(): JavaPlugin = this as JavaPlugin