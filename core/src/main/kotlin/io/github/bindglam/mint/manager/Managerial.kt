package io.github.bindglam.mint.manager

interface Managerial {
    fun preload(context: Context) {
    }

    fun start(context: Context) {
    }

    fun end(context: Context) {
    }

    fun priority(): Priority = Priority.empty()

    data class Priority(val start: Int, val end: Int) {
        companion object {
            private val EMPTY = Priority(0, 0)
            fun empty() = EMPTY

            fun of(start: Int, end: Int) = Priority(start, end)
        }
    }
}