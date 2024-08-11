package us.mytheria.bloblib.entities;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public interface Locatable extends Spatial {
    @Nullable
    World getWorld();

    @Override
    default Location toLocation() {
        Vector vector = toVector();
        return new Location(getWorld(), vector.getX(), vector.getY(), vector.getZ(), getYaw(), getPitch());
    }

    @Override
    default boolean isLocatable() {
        return getWorld() != null;
    }
}
