package us.mytheria.bloblib.managers.asset;

import me.anjoismysign.aesthetic.NamingConventions;
import me.anjoismysign.holoworld.asset.AssetGenerator;
import me.anjoismysign.holoworld.asset.DataAsset;
import me.anjoismysign.holoworld.asset.DataAssetEntry;
import me.anjoismysign.holoworld.manager.GeneratorManager;
import me.anjoismysign.holoworld.manager.SingletonManagerFactory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.managers.BlobLibConfigManager;

import java.io.File;
import java.util.Set;

public record SimpleBukkitGeneratorManager<T extends DataAsset>(
        @NotNull Plugin plugin,
        @NotNull GeneratorManager<T> generatorManager)
        implements BukkitGeneratorManager<T> {

    public static <T extends DataAsset> SimpleBukkitGeneratorManager<T> of(
            @NotNull Class<? extends AssetGenerator<T>> clazz,
            @NotNull Plugin plugin,
            @NotNull String name) {
        File parentDirectory = new File(plugin.getDataFolder(), NamingConventions.toSnakeCase(name));
        boolean verbose = BlobLibConfigManager.getInstance().isVerbose();
        return new SimpleBukkitGeneratorManager<>(plugin, SingletonManagerFactory.INSTANCE.generatorManager(clazz, parentDirectory, verbose ? plugin.getLogger() : null));
    }

    @Override
    public @NotNull Class<? extends AssetGenerator<T>> generatorClass() {
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
    public boolean add(@NotNull AssetGenerator<T> assetGenerator) {
        return generatorManager.add(assetGenerator);
    }

    @Override
    public void reload() {
        generatorManager.reload();
    }
}
