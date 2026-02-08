package com.bindglam.mint.manager

import com.bindglam.mint.MintConfiguration
import com.bindglam.mint.language.Language
import com.bindglam.mint.utils.Constants
import com.bindglam.mint.utils.plugin
import java.io.File

object LanguageManager : Managerial, Reloadable {
    private val builtInLanguages = listOf("english", "korean")
    private val langsFolder = File("plugins/${Constants.PLUGIN_NAME}/langs")

    private val langs = hashMapOf<String, Language>()

    private lateinit var config: MintConfiguration

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