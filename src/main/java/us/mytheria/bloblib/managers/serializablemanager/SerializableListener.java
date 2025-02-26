package us.mytheria.bloblib.managers.serializablemanager;

import me.anjoismysign.psa.lehmapp.LehmappSerializable;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.entities.BlobListener;
import us.mytheria.bloblib.managers.BlobPlugin;

import java.util.Objects;

public interface SerializableListener<T extends LehmappSerializable> extends BlobListener {
    Class<T> serializableClass();

    default String pluginName() {
        return getListenerManager().getPlugin().getName();
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
