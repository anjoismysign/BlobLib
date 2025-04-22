package us.mytheria.bloblib.managers.asset;

import me.anjoismysign.holoworld.asset.DataAsset;
import me.anjoismysign.holoworld.manager.AssetManager;
import me.anjoismysign.skeramidcommands.command.CommandTarget;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.logging.Logger;

public interface BukkitAssetManager<T extends DataAsset> extends AssetManager<T>, CommandTarget<T> {

    @NotNull
    Plugin plugin();

    @Override
    default @NotNull Logger logger() {
        return plugin().getLogger();
    }

    @Override
    default List<String> get() {
        return map().keySet().stream().toList();
    }

    @Override
    default @Nullable T parse(String s) {
        return map().get(s);
    }

}
