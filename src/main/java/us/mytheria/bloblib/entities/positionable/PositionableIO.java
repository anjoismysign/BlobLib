package us.mytheria.bloblib.entities.positionable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.translatable.BlobTranslatablePositionable;
import us.mytheria.bloblib.entities.translatable.TranslatablePositionable;
import us.mytheria.bloblib.exception.ConfigurationFieldException;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public enum PositionableIO {
    INSTANCE;

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
     * Serializes a Location randomly
     *
     * @param location The location in context
     * @return The randomly generated reference
     */
    public String writeRandom(@NotNull Location location) {
        Objects.requireNonNull(location, "'location' cannot be null");
        File directory = BlobLib.getInstance().getTranslatablePositionableManager().getAssetDirectory();
        UUID random = UUID.randomUUID();
        String key = random.toString();
        Positionable positionable = Positionable.of(location);
        BlobTranslatablePositionable blobTranslatablePositionable = new BlobTranslatablePositionable(
                key,
                "en_us",
                "Change me later!",
                positionable
        );
        File file = new File(directory, key + ".yml");
        directory.mkdirs();
        try {
            file.createNewFile();
        } catch ( IOException exception ) {
            throw new RuntimeException(exception);
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        write(configuration, blobTranslatablePositionable);
        try {
            configuration.save(file);
        } catch ( IOException exception ) {
            throw new RuntimeException(exception);
        }
        return key;
    }

    public void write(@NotNull ConfigurationSection at,
                      @NotNull TranslatablePositionable translatablePositionable) {
        Objects.requireNonNull(translatablePositionable, "'translatablePositionable' cannot be null");
        Positionable positionable = translatablePositionable.get();
        String locale = translatablePositionable.getLocale();
        String display = translatablePositionable.getDisplay();

        at.set("Locale", locale);
        at.set("Display", display);
        write(at, positionable);
    }

    public void write(@NotNull ConfigurationSection at,
                      @NotNull Positionable positionable) {
        Objects.requireNonNull(at, "'at' cannot be null");
        Objects.requireNonNull(positionable, "'positionable' cannot be null");

        // Write the basic position data
        at.set("X", positionable.getX());
        at.set("Y", positionable.getY());
        at.set("Z", positionable.getZ());

        // Check if the positionable is a Spatial and write Yaw and Pitch
        if (positionable instanceof Spatial) {
            Spatial spatial = (Spatial) positionable;
            at.set("Yaw", spatial.getYaw());
            at.set("Pitch", spatial.getPitch());
        }

        // Check if the positionable is a Locatable and write the World
        if (positionable instanceof Locatable) {
            Locatable locatable = (Locatable) positionable;
            World world = locatable.getWorld();
            if (world != null) {
                at.set("World", world.getName());
            }
        }
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
        return new Locatable() {
            @NotNull
            public World getWorld() {
                return Objects.requireNonNull(Bukkit.getWorld(worldName), "World not found: " + worldName);
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
