package io.github.anjoismysign.bloblib.managers.asset;

import io.github.anjoismysign.holoworld.asset.DataAsset;
import io.github.anjoismysign.holoworld.manager.GeneratorManager;
import io.github.anjoismysign.skeramidcommands.command.CommandTarget;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.logging.Logger;

public interface BukkitGeneratorManager<T extends DataAsset> extends GeneratorManager<T>, CommandTarget<T> {

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
