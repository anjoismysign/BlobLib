package us.mytheria.bloblib.entities.area;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface Area {

    @NotNull
    AreaType getType();

    @NotNull
    String getWorldName();

    @NotNull
    default World getWorld() {
        return Objects.requireNonNull(Bukkit.getWorld(getWorldName()), "World not found: " + getWorldName());
    }

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
