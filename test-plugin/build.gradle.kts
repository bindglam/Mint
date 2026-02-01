import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    id("paper-conventions")
    alias(libs.plugins.resourceFactory.paper)
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(project(":api"))
    implementation("dev.jorel:commandapi-bukkit-shade:10.1.2")
}

paperPluginYaml {
    name = "${rootProject.name}-TestPlugin"
    version = rootProject.version.toString()
    main = "$group.test.GoldEngineTestPlugin"
    apiVersion = "1.20"
    author = "Bindglam"
    dependencies {
        server(name = "GoldEngine", load = PaperPluginYaml.Load.BEFORE, required = true, joinClasspath = true)
    }
}

val groupString = group.toString()
tasks {
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
        prefix("dev.jorel.commandapi")
    }
}