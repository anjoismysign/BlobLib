package us.mytheria.bloblib.entities.area;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface Area {
    @NotNull
    World getWorld();

    boolean isInside(@NotNull Location location);

    default boolean isInside(@NotNull Entity entity) {
        Objects.requireNonNull(entity, "'entity' cannot be null");
        return isInside(entity.getLocation());
    }

    default boolean isInside(@NotNull Block block) {
        Objects.requireNonNull(block, "'block' cannot be null");
        return isInside(block.getLocation());
    }
}
