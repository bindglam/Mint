import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    id("paper-conventions")
    alias(libs.plugins.resourceFactory.paper)
}

dependencies {
    implementation(project(":api"))
    compileOnly("com.github.bindglam:ConfigLib:1.0.0")
    compileOnly("org.incendo:cloud-paper:2.0.0-beta.14")
    //compileOnly("org.incendo:cloud-minecraft-extras:2.0.0-beta.14")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude("org.bukkit")
    }
}

paperPluginYaml {
    name = rootProject.name
    version = rootProject.version.toString()
    main = "$group.GoldEnginePluginImpl"
    loader = "$group.GoldEnginePluginLoader"
    apiVersion = "1.20"
    author = "Bindglam"
    dependencies {
        server(name = "Vault", load = PaperPluginYaml.Load.BEFORE, required = false, joinClasspath = true)
        server(name = "PlaceholderAPI", load = PaperPluginYaml.Load.BEFORE, required = false, joinClasspath = true)
    }
}