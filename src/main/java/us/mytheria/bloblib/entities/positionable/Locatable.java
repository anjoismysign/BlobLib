package us.mytheria.bloblib.entities.positionable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface Locatable extends Spatial {
    @NotNull
    World getWorld();

    @NotNull
    @Override
    default Location toLocation() {
        Vector vector = toVector();
        return new Location(getWorld(), vector.getX(), vector.getY(), vector.getZ(), getYaw(), getPitch());
    }

    @Override
    @NotNull
    default PositionableType getPositionableType() {
        return PositionableType.LOCATABLE;
    }
}
