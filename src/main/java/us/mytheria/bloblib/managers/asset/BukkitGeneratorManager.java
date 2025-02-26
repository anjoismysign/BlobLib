package us.mytheria.bloblib.managers.asset;

import me.anjoismysign.holoworld.asset.DataAsset;
import me.anjoismysign.holoworld.manager.GeneratorManager;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public interface BukkitGeneratorManager<T extends DataAsset> extends GeneratorManager<T> {

    @NotNull
    Plugin plugin();

    @Override
    default @NotNull Logger logger() {
        return plugin().getLogger();
    }

}
