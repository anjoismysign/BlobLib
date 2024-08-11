package us.mytheria.bloblib.entities.positionable;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.exception.ConfigurationFieldException;
import us.mytheria.bloblib.utilities.SerializationLib;

import java.util.Objects;

public class PositionableReader {
    private static PositionableReader instance;

    public static PositionableReader getInstance() {
        if (instance == null)
            instance = new PositionableReader();
        return instance;
    }

    private PositionableReader() {
    }

    /**
     * Reads a Positionable from a ConfigurationSection.
     *
     * @param parent The ConfigurationSection to read from.
     *               It is implied that this ConfigurationSection holds "Positionable" child
     *               ConfigurationSection which holds all related data in order to
     *               create a Positionable.
     * @return The Positionable, may be any of the three possible instances.
     */
    @NotNull
    public Positionable readAsDefaultParent(@NotNull ConfigurationSection parent) {
        Objects.requireNonNull(parent, "'parent' cannot be null");
        ConfigurationSection section = parent.getConfigurationSection("Positionable");
        if (section == null)
            throw new ConfigurationFieldException("'Positionable' is not valid or set");
        return read(section);
    }

    /**
     * Reads a Positionable from a ConfigurationSection.
     *
     * @param section The ConfigurationSection to read from.
     *                It is implied that this ConfigurationSection holds all related data
     *                in order to create a Positionable.
     * @return The Positionable, may be any of the three possible instances.
     */
    @NotNull
    public Positionable read(@NotNull ConfigurationSection section) {
        Objects.requireNonNull(section, "'section' cannot be null");
        if (!section.isDouble("X"))
            throw new ConfigurationFieldException("'X' is not valid or set");
        if (!section.isDouble("Y"))
            throw new ConfigurationFieldException("'Y' is not valid or set");
        if (!section.isDouble("Z"))
            throw new ConfigurationFieldException("'Z' is not valid or set");
        double x = section.getDouble("X");
        double y = section.getDouble("Y");
        double z = section.getDouble("Z");
        if (!section.isDouble("Yaw") || !section.isDouble("Pitch")) {
            return new Positionable() {
                public double getX() {
                    return x;
                }

                public double getY() {
                    return y;
                }

                public double getZ() {
                    return z;
                }
            };
        }
        float yaw = (float) section.getDouble("Yaw");
        float pitch = (float) section.getDouble("Pitch");
        if (!section.isString("World")) {
            return new Spatial() {
                public float getYaw() {
                    return yaw;
                }

                public float getPitch() {
                    return pitch;
                }

                public double getX() {
                    return x;
                }

                public double getY() {
                    return y;
                }

                public double getZ() {
                    return z;
                }
            };
        }
        String worldName = section.getString("World");
        @NotNull World world = SerializationLib.deserializeWorld(worldName);
        return new Locatable() {
            @NotNull
            public World getWorld() {
                return world;
            }

            public float getYaw() {
                return yaw;
            }

            public float getPitch() {
                return pitch;
            }

            public double getX() {
                return x;
            }

            public double getY() {
                return y;
            }

            public double getZ() {
                return z;
            }
        };
    }
}
