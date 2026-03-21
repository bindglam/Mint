import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    id("paper-conventions")
    alias(libs.plugins.resourceFactory.paper)
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(project(":api"))
    compileOnly("org.incendo:cloud-paper:2.0.0-beta.14")
}

paperPluginYaml {
    name = "${rootProject.name}-Addon"
    version = rootProject.version.toString()
    main = "$group.addon.MintAddonPlugin"
    loader = "$group.addon.MintAddonPluginLoader"
    apiVersion = "1.20"
    author = "Bindglam"
    foliaSupported = true
    dependencies {
        server(name = "Mint", load = PaperPluginYaml.Load.BEFORE, required = true, joinClasspath = true)
    }
}

val groupString = group.toString()
tasks {
    jar {
        finalizedBy(shadowJar)
    }

    shadowJar {
        archiveBaseName = "${rootProject.name}-Addon"
        archiveClassifier = ""

        dependencies {
            exclude(dependency("org.jetbrains:annotations:13.0")); exclude(dependency("org.jetbrains:annotations:23.0.0")); exclude(dependency("org.jetbrains:annotations:26.0.2"))
        }

        fun prefix(pattern: String) {
            relocate(pattern, "$groupString.shaded.$pattern")
        }
    }
}