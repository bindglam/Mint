package com.bindglam.mint.addon;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.ClassPathLibrary;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public final class MintAddonPluginLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        classpathBuilder.addLibrary(mavenCentral());
    }

    private ClassPathLibrary mavenCentral() {
        var resolver = new MavenLibraryResolver();

        resolver.addRepository(new RemoteRepository.Builder("central", "default", "https://maven-central.storage-download.googleapis.com/maven2").build());

        resolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-paper:2.0.0-beta.14"), null));

        return resolver;
    }
}
