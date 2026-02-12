import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    id("paper-conventions")
    alias(libs.plugins.resourceFactory.paper)
}

dependencies {
    implementation(project(":api"))
    implementation("org.bstats:bstats-bukkit:3.1.0")
    compileOnly("com.github.bindglam:ConfigLib:1.0.0")
    compileOnly("org.incendo:cloud-paper:2.0.0-beta.14")
    //compileOnly("org.incendo:cloud-minecraft-extras:2.0.0-beta.14")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude("org.bukkit")
    }
    compileOnly("org.semver4j:semver4j:6.0.0")
}

paperPluginYaml {
    name = rootProject.name
    version = rootProject.version.toString()
    main = "$group.MintPluginImpl"
    loader = "$group.MintPluginLoader"
    apiVersion = "1.20"
    author = "Bindglam"
    foliaSupported = true
    dependencies {
        server(name = "Vault", load = PaperPluginYaml.Load.BEFORE, required = false, joinClasspath = true)
        server(name = "PlaceholderAPI", load = PaperPluginYaml.Load.BEFORE, required = false, joinClasspath = true)
    }
}