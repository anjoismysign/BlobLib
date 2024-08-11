package us.mytheria.bloblib.entities;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Spatial extends Positionable {
    float getYaw();

    float getPitch();

    @NotNull
    default Location toLocation() {
        return toLocation(null);
    }

    @NotNull
    default Location toLocation(@Nullable World world) {
        Vector vector = toVector();
        return new Location(world, vector.getX(), vector.getY(), vector.getZ(), getYaw(), getPitch());
    }
}
