plugins {
    id("standard-conventions")
    alias(libs.plugins.runTask.paper)
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(project(":core"))
}

val groupString = group.toString()
val versionString = version.toString()
val mcVersionString = property("minecraft_version").toString()

tasks {
    runServer {
        version(mcVersionString)

        downloadPlugins {
            pluginJars(project("test-plugin").tasks.shadowJar.flatMap {
                it.archiveFile
            })
            modrinth("vaultunlocked", "2.18.0")
            github("SkriptLang", "Skript", "2.12.2", "Skript-2.12.2.jar")
            github("SkriptLang", "skript-reflect", "v2.6.1", "skript-reflect-2.6.1.jar")
        }
    }

    jar {
        finalizedBy(shadowJar)
    }

    shadowJar {
        archiveClassifier = ""

        dependencies {
            exclude(dependency("org.jetbrains:annotations:13.0")); exclude(dependency("org.jetbrains:annotations:23.0.0")); exclude(dependency("org.jetbrains:annotations:26.0.2"))
        }

        fun prefix(pattern: String) {
            relocate(pattern, "$groupString.shaded.$pattern")
        }
        prefix("kotlin")
        //prefix("org.incendo.cloud")
        //prefix("org.slf4j")
        //prefix("io.leangen.geantyref")
        //prefix("com.bindglam.config")
        //prefix("com.alibaba.fastjson2")
        //prefix("com.zaxxer.hikari")
    }
}