package us.mytheria.bloblib.entities;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Positionable {
    double getX();

    double getY();

    double getZ();

    @NotNull
    default Vector toVector() {
        return new Vector(getX(), getY(), getZ());
    }

    @NotNull
    default Location toLocation() {
        return toLocation(null);
    }

    @NotNull
    default Location toLocation(@Nullable World world) {
        Vector vector = toVector();
        return new Location(world, vector.getX(), vector.getY(), vector.getZ());
    }

    default boolean isLocatable() {
        return false;
    }
}
