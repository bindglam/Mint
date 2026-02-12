package com.bindglam.mint

import io.papermc.paper.plugin.loader.PluginClasspathBuilder
import io.papermc.paper.plugin.loader.PluginLoader
import io.papermc.paper.plugin.loader.library.ClassPathLibrary
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.RemoteRepository

@Suppress("unstableApiUsage")
class MintPluginLoader : PluginLoader {
    override fun classloader(classpathBuilder: PluginClasspathBuilder) {
        classpathBuilder.addLibrary(mavenCentral())
        classpathBuilder.addLibrary(jitpack())
    }

    private fun mavenCentral(): ClassPathLibrary {
        val resolver = MavenLibraryResolver()

        resolver.addRepository(RemoteRepository.Builder("central", "default", "https://maven-central.storage-download.googleapis.com/maven2").build())

        resolver.addDependency(Dependency(DefaultArtifact("com.zaxxer:HikariCP:7.0.2"), null))
        resolver.addDependency(Dependency(DefaultArtifact("org.incendo:cloud-paper:2.0.0-beta.14"), null))
        resolver.addDependency(Dependency(DefaultArtifact("org.semver4j:semver4j:6.0.0"), null))

        return resolver
    }

    private fun jitpack(): ClassPathLibrary {
        val resolver = MavenLibraryResolver()

        resolver.addRepository(RemoteRepository.Builder("jitpack", "default", "https://jitpack.io").build())

        resolver.addDependency(Dependency(DefaultArtifact("com.github.bindglam:ConfigLib:1.0.0"), null))

        return resolver
    }
}