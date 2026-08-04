package io.github.bindglam.mint.utils

import io.github.bindglam.mint.manager.LanguageManager

fun lang(key: String, vararg args: Any) = LanguageManager.lang().get(key, *args)