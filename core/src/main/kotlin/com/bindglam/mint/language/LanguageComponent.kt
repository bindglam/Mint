package com.bindglam.mint.language

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

class LanguageComponent(private val raw: String) {
    fun component(vararg args: Any): Component {
        return MiniMessage.miniMessage().deserialize(raw.format(*args))
    }
}