package us.mytheria.bloblib.entities.positionable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Spatial extends Positionable {
    float getYaw();

    float getPitch();

    @Override
    @NotNull
    default Location toLocation(@Nullable World world) {
        Vector vector = toVector();
        return new Location(world, vector.getX(), vector.getY(), vector.getZ(), getYaw(), getPitch());
    }

    @Override
    @NotNull
    default PositionableType getPositionableType() {
        return PositionableType.SPATIAL;
    }
}
