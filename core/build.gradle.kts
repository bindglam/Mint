import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    id("paper-conventions")
    alias(libs.plugins.resourceFactory.paper)
}

dependencies {
    implementation(project(":api"))
    implementation("dev.jorel:commandapi-bukkit-shade:10.1.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude("org.bukkit")
    }
}

paperPluginYaml {
    name = rootProject.name
    version = rootProject.version.toString()
    main = "$group.GoldEnginePluginImpl"
    apiVersion = "1.20"
    author = "Bindglam"
    dependencies {
        server(name = "Vault", load = PaperPluginYaml.Load.BEFORE, required = false, joinClasspath = true)
        server(name = "PlaceholderAPI", load = PaperPluginYaml.Load.BEFORE, required = false, joinClasspath = true)
    }
}