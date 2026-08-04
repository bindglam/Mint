package io.github.bindglam.mint.language

import net.kyori.adventure.text.Component
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class Language(val name: String) {
    private val map = hashMapOf<String, LanguageComponent>()

    fun load(file: File) {
        val config = YamlConfiguration.loadConfiguration(file)

        for (key in config.getKeys(false)) {
            this.map[key] = LanguageComponent(config.getString(key)!!)
        }
    }

    fun get(key: String, vararg args: Any): Component {
        val langComp = this.map[key]
        if (langComp != null) return langComp.component(*args)
        return Component.text(key)
    }
}