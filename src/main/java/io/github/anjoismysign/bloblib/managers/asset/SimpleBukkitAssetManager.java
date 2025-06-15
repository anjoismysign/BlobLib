package io.github.anjoismysign.bloblib.managers.asset;

import io.github.anjoismysign.bloblib.managers.BlobLibConfigManager;
import io.github.anjoismysign.aesthetic.NamingConventions;
import io.github.anjoismysign.holoworld.asset.DataAsset;
import io.github.anjoismysign.holoworld.asset.DataAssetEntry;
import io.github.anjoismysign.holoworld.manager.AssetManager;
import io.github.anjoismysign.holoworld.manager.SingletonManagerFactory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Set;

public record SimpleBukkitAssetManager<T extends DataAsset>(
        @NotNull Plugin plugin,
        @NotNull AssetManager<T> assetManager)
        implements BukkitAssetManager<T> {

    public static <T extends DataAsset> SimpleBukkitAssetManager<T> of(
            @NotNull Class<T> clazz,
            @NotNull Plugin plugin,
            @NotNull String name) {
        File parentDirectory = new File(plugin.getDataFolder(), NamingConventions.toSnakeCase(name));
        boolean verbose = BlobLibConfigManager.getInstance().isVerbose();
        return new SimpleBukkitAssetManager<>(plugin, SingletonManagerFactory.INSTANCE.assetManager(clazz, parentDirectory, verbose ? plugin.getLogger() : null));
    }

    @Override
    public @NotNull Class<T> assetClass() {
        return assetManager.assetClass();
    }

    @Override
    public @NotNull File directory() {
        return assetManager.directory();
    }

    @Override
    public @Nullable DataAssetEntry<T> fetchAsset(@NotNull String identifier) {
        return assetManager.fetchAsset(identifier);
    }

    @Override
    public @NotNull Set<String> getIdentifiers() {
        return assetManager.getIdentifiers();
    }

    @Override
    public boolean add(@NotNull T t) {
        return assetManager.add(t);
    }

    @Override
    public void reload() {
        assetManager.reload();
    }
}
