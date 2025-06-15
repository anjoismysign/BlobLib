package io.github.anjoismysign.bloblib.entities.positionable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface Positionable {

    static Positionable of(@NotNull Location location) {
        Objects.requireNonNull(location, "'location' cannot be null");
        @Nullable World world = location.getWorld();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        if (world != null)
            return new Locatable() {
                @Override
                public @NotNull World getWorld() {
                    return world;
                }

                @Override
                public float getYaw() {
                    return yaw;
                }

                @Override
                public float getPitch() {
                    return pitch;
                }

                @Override
                public double getX() {
                    return x;
                }

                @Override
                public double getY() {
                    return y;
                }

                @Override
                public double getZ() {
                    return z;
                }
            };
        return new Spatial() {
            @Override
            public float getYaw() {
                return yaw;
            }

            @Override
            public float getPitch() {
                return pitch;
            }

            @Override
            public double getX() {
                return x;
            }

            @Override
            public double getY() {
                return y;
            }

            @Override
            public double getZ() {
                return z;
            }
        };
    }

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

    /**
     * Gets the PositionableType of this Positionable
     *
     * @return The PositionableType
     */
    @NotNull
    default PositionableType getPositionableType() {
        return PositionableType.POSITIONABLE;
    }
}
