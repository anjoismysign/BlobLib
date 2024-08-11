package us.mytheria.bloblib.entities;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface Positionable {
    double getX();

    double getY();

    double getZ();

    @NotNull
    default Vector toVector() {
        return new Vector(getX(), getY(), getZ());
    }
}
