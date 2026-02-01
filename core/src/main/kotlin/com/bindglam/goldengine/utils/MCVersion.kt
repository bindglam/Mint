package com.bindglam.goldengine.utils

import org.bukkit.Bukkit

data class MCVersion(
    val first: Int,
    val second: Int,
    val third: Int
): Comparable<MCVersion> {
    companion object {
        val current = MCVersion(Bukkit.getBukkitVersion().substringBefore('-'))

        val v1_21_4 = MCVersion(1, 21, 4)

        private val comparator = Comparator.comparing { v: MCVersion ->
            v.first
        }.thenComparing { v: MCVersion ->
            v.second
        }.thenComparing { v: MCVersion ->
            v.third
        }
    }

    constructor(string: String): this(string.split('.'))
    constructor(string: List<String>): this(
        if (string.isNotEmpty()) string[0].toInt() else 0,
        if (string.size > 1) string[1].toInt() else 0,
        if (string.size > 2) string[2].toInt() else 0
    )
    override fun compareTo(other: MCVersion): Int {
        return comparator.compare(this, other)
    }

    override fun toString(): String {
        return if (third == 0) "$first.$second" else "$first.$second.$third"
    }
}