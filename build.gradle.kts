import xyz.jpenilla.runpaper.task.RunServer

plugins {
    id("standard-conventions")
    alias(libs.plugins.runTask.paper)
    alias(libs.plugins.shadow)
    alias(libs.plugins.minotaur)
}

dependencies {
    implementation(project(":core"))
}

val groupString = group.toString()
val versionString = version.toString()
val mcVersionString = property("minecraft_version").toString()
val supportedVersions = listOf(
    "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6",
    "1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11"
)

val runServerAction = Action<RunServer> {
    version(mcVersionString)

    downloadPlugins {
        pluginJars(project("test-plugin").tasks.shadowJar.flatMap {
            it.archiveFile
        })
        modrinth("vaultunlocked", "2.18.0")
        modrinth("placeholderapi", "2.12.1")
        //github("SkriptLang", "Skript", "2.12.2", "Skript-2.12.2.jar")
        //github("SkriptLang", "skript-reflect", "v2.6.1", "skript-reflect-2.6.1.jar")
    }
}

runPaper.folia.registerTask(op = runServerAction)

tasks {
    runServer {
        runServerAction.execute(this)
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
        prefix("org.bstats")
    }
}

modrinth {
    token = System.getenv("MODRINTH_API_TOKEN")
    projectId = "mint-economy"
    syncBodyFrom = rootProject.file("README.md").readText()
    versionType = "release"
    changelog = rootProject.file("changelogs/$versionString.md").readText()
    versionNumber = versionString
    versionName = versionString
    modrinth {
        uploadFile.set(tasks.shadowJar)
        gameVersions = supportedVersions
        loaders = listOf("paper", "folia", "purpur")
        dependencies {
            optional.project(
                "vaultunlocked"
            )
        }
    }
}