pluginManagement {
    plugins {
        kotlin("plugin.lombok") version "2.4.10"
    }
}

rootProject.name = "Mint"

include("api", "core")