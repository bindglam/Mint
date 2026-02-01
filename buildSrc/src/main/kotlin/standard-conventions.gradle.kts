plugins {
    id("java-library")
    kotlin("jvm")
}

group = "com.bindglam.goldengine"
version = property("plugin_version").toString()

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.alibaba.fastjson2:fastjson2:2.0.60")
    implementation("com.zaxxer:HikariCP:4.0.3")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

kotlin {
    jvmToolchain(21)
}