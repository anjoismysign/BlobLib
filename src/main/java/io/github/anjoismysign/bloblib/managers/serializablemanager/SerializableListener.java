package io.github.anjoismysign.bloblib.managers.serializablemanager;

import io.github.anjoismysign.bloblib.entities.BlobListener;
import io.github.anjoismysign.bloblib.managers.BlobPlugin;
import io.github.anjoismysign.psa.lehmapp.LehmappSerializable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface SerializableListener<T extends LehmappSerializable> extends BlobListener {
    Class<T> serializableClass();

    @NotNull Plugin plugin();

    default String pluginName() {
        return plugin().getName();
    }

    @NotNull
    default BukkitSerializableManager<T> getSerializableManager() {
        if (!(Bukkit.getPluginManager().getPlugin(pluginName()) instanceof BlobPlugin blobPlugin))
            throw new RuntimeException(pluginName() + " is not instance of BlobPlugin!");
        BukkitSerializableManager<? extends LehmappSerializable> serializableManager = blobPlugin.getSerializableManagers().get(serializableClass());
        Objects.requireNonNull(serializableManager, pluginName() + " doesn't implement BlobSerializableManager<" + serializableClass().getSimpleName() + ">");
        return (BukkitSerializableManager<T>) serializableManager;
    }
}
