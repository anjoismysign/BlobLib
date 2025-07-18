package io.github.anjoismysign.bloblib.managers.asset;

import io.github.anjoismysign.aesthetic.NamingConventions;
import io.github.anjoismysign.bloblib.managers.BlobLibConfigManager;
import io.github.anjoismysign.holoworld.asset.DataAsset;
import io.github.anjoismysign.holoworld.asset.DataAssetEntry;
import io.github.anjoismysign.holoworld.asset.IdentityGeneration;
import io.github.anjoismysign.holoworld.asset.IdentityGenerator;
import io.github.anjoismysign.holoworld.manager.IdentityManager;
import io.github.anjoismysign.holoworld.manager.SingletonManagerFactory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Set;

public record SimpleBukkitIdentityManager<T extends DataAsset>(
        @NotNull Plugin plugin,
        @NotNull IdentityManager<T> generatorManager)
        implements BukkitIdentityManager<T> {

    public static <T extends DataAsset> SimpleBukkitIdentityManager<T> of(
            @NotNull Class<? extends IdentityGenerator<T>> clazz,
            @NotNull Plugin plugin,
            @NotNull String name,
            boolean failOnNullField) {
        File parentDirectory = new File(plugin.getDataFolder(), NamingConventions.toSnakeCase(name));
        boolean verbose = BlobLibConfigManager.getInstance().isVerbose();
        return new SimpleBukkitIdentityManager<>(plugin, SingletonManagerFactory.INSTANCE.identityManager(
                clazz,
                parentDirectory,
                verbose ? plugin.getLogger() : null,
                failOnNullField));
    }

    @Override
    public @NotNull Class<? extends IdentityGenerator<T>> generatorClass() {
        return generatorManager.generatorClass();
    }

    @Override
    public @NotNull File directory() {
        return generatorManager.directory();
    }

    @Override
    public @Nullable DataAssetEntry<T> fetchGeneration(@NotNull String identifier) {
        return generatorManager.fetchGeneration(identifier);
    }

    @Override
    public @NotNull Set<String> getIdentifiers() {
        return generatorManager.getIdentifiers();
    }

    @Override
    public boolean add(@NotNull IdentityGeneration<T> assetGenerator) {
        return generatorManager.add(assetGenerator);
    }

    @Override
    public void reload() {
        generatorManager.reload();
    }
}
