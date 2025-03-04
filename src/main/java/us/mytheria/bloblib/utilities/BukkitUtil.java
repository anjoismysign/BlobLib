package us.mytheria.bloblib.utilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BukkitUtil {

    /**
     * Converts a Location object to String so later can be converted back to Location.
     *
     * @see Location
     */
    public static String locationToString(Location loc) {
        if (loc == null)
            return "null";
        return loc.getWorld().getName() + "%loc:%" + loc.getX() + "%loc:%" + loc.getY() + "%loc:%" + loc.getZ();
    }

    /**
     * Converts String that were made with locationToString into Location object.
     *
     * @see Location
     */
    public static Location stringToLocation(String string) {
        if (string.equals("null"))
            return null;
        String[] split = string.split("%loc:%");
        return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]),
                Double.parseDouble(split[3]));
    }

    public static ConfigurationSection serializeLocation(Location location, ConfigurationSection section) {
        section.set("World", location.getWorld().getName());
        section.set("X", location.getX());
        section.set("Y", location.getY());
        section.set("Z", location.getZ());
        section.set("Yaw", location.getYaw());
        section.set("Pitch", location.getPitch());
        return section;
    }

    public static ConfigurationSection serializeLocation(Location location, YamlConfiguration config,
                                                         String path) {
        ConfigurationSection section = config.createSection(path);
        section.set("World", location.getWorld().getName());
        section.set("X", location.getX());
        section.set("Y", location.getY());
        section.set("Z", location.getZ());
        section.set("Yaw", location.getYaw());
        section.set("Pitch", location.getPitch());
        return section;
    }

    /**
     * Prints Location in a more readable way.
     *
     * @see Location
     */
    public static String printLocation(Location location) {
        return location.getWorld().getName() + "-" + location.toVector() + " (World-X,Y,Z)";
    }

    @Nullable
    public static List<String> serializeBlockSet(Set<Block> set) {
        if (set == null)
            return null;
        List<String> blocks = new ArrayList<>();
        for (Block block : set) {
            blocks.add(locationToString(block.getLocation()));
        }
        return blocks;
    }

    @Nullable
    public static Set<Block> deserializeBlockSet(List<String> serialized) {
        if (serialized == null)
            return null;
        Set<Block> blocks = new HashSet<>();
        for (String string : serialized) {
            blocks.add(stringToLocation(string).getBlock());
        }
        return blocks;
    }

    @Nullable
    public static List<String> serializeVectorSet(Set<Vector> set) {
        if (set == null)
            return null;
        List<String> vectors = new ArrayList<>();
        for (Vector vector : set) {
            vectors.add(vector.getX() + "%" + vector.getY() + "%" + vector.getZ());
        }
        return vectors;
    }

    @Nullable
    public static Set<Vector> deserializeVectorSet(List<String> serialized) {
        if (serialized == null)
            return null;
        Set<Vector> vectors = new HashSet<>();
        for (String string : serialized) {
            String[] split = string.split("%");
            vectors.add(new Vector(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])));
        }
        return vectors;
    }

    @Nullable
    public static List<String> serializeBlockVectorSet(Set<BlockVector> set) {
        if (set == null)
            return null;
        List<String> blocks = new ArrayList<>();
        for (BlockVector blockVectors : set) {
            blocks.add(blockVectors.getBlockX() + "%" + blockVectors.getBlockY() + "%" + blockVectors.getBlockZ());
        }
        return blocks;
    }

    @Nullable
    public static Set<BlockVector> deserializeBlockVectorSet(List<String> serialized) {
        if (serialized == null)
            return null;
        Set<BlockVector> blockVectors = new HashSet<>();
        for (String string : serialized) {
            String[] split = string.split("%");
            blockVectors.add(new BlockVector(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])));
        }
        return blockVectors;
    }
}
