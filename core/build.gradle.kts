import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    id("paper-conventions")
    alias(libs.plugins.resourceFactory.paper)
    kotlin("plugin.lombok")
}

dependencies {
    implementation(project(":api"))
    implementation("org.bstats:bstats-bukkit:3.1.0")
    compileOnly("com.github.bindglam:ConfigLib:1.0.0")
    compileOnly("com.github.bindglam.DatabaseLib:DatabaseLib:2.1.1")
    compileOnly("net.milkbowl.vault:VaultUnlockedAPI:2.20")
    compileOnly("me.clip:placeholderapi:2.12.3")
    compileOnly("org.semver4j:semver4j:6.0.0")
}

paperPluginYaml {
    name = rootProject.name
    version = rootProject.version.toString()
    main = "$group.MintPluginImpl"
    loader = "$group.MintPluginLoader"
    apiVersion = "26.1"
    author = "Bindglam"
    foliaSupported = true
    dependencies {
        server(name = "Vault", load = PaperPluginYaml.Load.BEFORE, required = false, joinClasspath = true)
        server(name = "PlaceholderAPI", load = PaperPluginYaml.Load.BEFORE, required = false, joinClasspath = true)
    }
}