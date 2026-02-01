package com.bindglam.goldengine.manager

import com.bindglam.goldengine.GoldEngineConfiguration
import com.bindglam.goldengine.language.Language
import com.bindglam.goldengine.utils.plugin
import java.io.File

object LanguageManager : Managerial, Reloadable {
    private val builtInLanguages = listOf("korean")
    private val langsFolder = File("plugins/GoldEngine/langs")

    private val langs = hashMapOf<String, Language>()

    private lateinit var config: GoldEngineConfiguration

    override fun start(context: Context) {
        this.config = context.config()

        if(!langsFolder.exists())
            langsFolder.mkdirs()

        builtInLanguages.forEach { name ->
            val file = File(langsFolder, "$name.yml")
            if(file.exists()) return@forEach
            file.createNewFile()

            context.plugin().plugin().getResource("langs/$name.yml")?.copyTo(file.outputStream())
        }

        langsFolder.listFiles().forEach { file ->
            val lang = Language(file.nameWithoutExtension).also { it.load(file) }
            langs[lang.name] = lang
        }
    }

    override fun end(context: Context) {
        langs.clear()
    }

    override fun reload(context: Context) {
        end(context)
        start(context)
    }

    fun lang() = langs[this.config.language.value()]!!
}