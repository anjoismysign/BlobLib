package us.mytheria.bloblib.managers.asset;

import me.anjoismysign.holoworld.asset.DataAsset;
import me.anjoismysign.holoworld.asset.DataAssetEntry;
import me.anjoismysign.holoworld.manager.AssetManager;
import me.anjoismysign.holoworld.manager.SingletonManagerFactory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.managers.BlobLibConfigManager;

import java.io.File;
import java.util.Locale;
import java.util.Set;

public record SimpleBukkitAssetManager<T extends DataAsset>(
        @NotNull Plugin plugin,
        @NotNull AssetManager<T> assetManager)
        implements BukkitAssetManager<T> {

    public static <T extends DataAsset> SimpleBukkitAssetManager<T> of(
            @NotNull Class<T> clazz,
            @NotNull Plugin plugin,
            @NotNull String name) {
        File parentDirectory = new File(plugin.getDataFolder(), name.toLowerCase(Locale.ROOT));
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
    public @NotNull String defaultLocale() {
        return assetManager.defaultLocale();
    }

    @Override
    public @Nullable DataAssetEntry<T> fetchAssetByLocale(@NotNull String identifier, @NotNull String locale) {
        return assetManager.fetchAssetByLocale(identifier, locale);
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
