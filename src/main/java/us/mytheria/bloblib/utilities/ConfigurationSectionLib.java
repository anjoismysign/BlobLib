package us.mytheria.bloblib.utilities;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConfigurationSectionLib {

    public static void serializeVectorList(List<Vector> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(vector -> stringList.add(vector.getX() + ", " + vector.getY() + ", " + vector.getZ()));
        section.set(path, stringList);
    }

    public static List<Vector> deserializeVectorList(ConfigurationSection section, String path) {
        List<BlockVector> test = new ArrayList<>();
        List<Vector> vectorList = new ArrayList<>();
        section.getStringList(path).forEach(string -> {
            String[] split = string.split(", ");
            vectorList.add(new Vector(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])));
        });
        return vectorList;
    }

    public static void serializeBlockVectorList(List<BlockVector> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(vector -> stringList.add(vector.getBlockX() + ", " + vector.getBlockY() + ", " + vector.getBlockZ()));
        section.set(path, stringList);
    }

    public static List<BlockVector> deserializeBlockVectorList(ConfigurationSection section, String path) {
        List<BlockVector> vectorList = new ArrayList<>();
        section.getStringList(path).forEach(string -> {
            String[] split = string.split(", ");
            vectorList.add(new BlockVector(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])));
        });
        return vectorList;
    }

    public static void serializeLocationList(List<Location> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(location -> stringList.add(location.getWorld().getName() + ", " + location.getX() + ", " + location.getY() + ", " + location.getZ() + ", " + location.getYaw() + ", " + location.getPitch()));
        section.set(path, stringList);
    }

    public static List<Location> deserializeLocationList(ConfigurationSection section, String path) {
        List<Location> locationList = new ArrayList<>();
        section.getStringList(path).forEach(string -> {
            String[] split = string.split(", ");
            locationList.add(new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5])));
        });
        return locationList;
    }

    public static void serializeBlockList(List<Block> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(block -> stringList.add(block.getWorld().getName() + ", " + block.getX() + ", " + block.getY() + ", " + block.getZ()));
        section.set(path, stringList);
    }

    public static List<Block> deserializeBlockList(ConfigurationSection section, String path) {
        List<Block> blockList = new ArrayList<>();
        section.getStringList(path).forEach(string -> {
            String[] split = string.split(", ");
            blockList.add(Bukkit.getWorld(split[0]).getBlockAt(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3])));
        });
        return blockList;
    }

    public static void serializeColorList(List<Color> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(color -> stringList.add(color.getRed() + ", " + color.getGreen() + ", " + color.getBlue()));
        section.set(path, stringList);
    }

    public static List<Color> deserializeColorList(ConfigurationSection section, String path) {
        List<Color> colorList = new ArrayList<>();
        section.getStringList(path).forEach(string -> {
            String[] split = string.split(", ");
            colorList.add(Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])));
        });
        return colorList;
    }

    public static void serializeOfflinePlayer(List<OfflinePlayer> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(player -> stringList.add(player.getUniqueId().toString()));
        section.set(path, stringList);
    }

    public static List<OfflinePlayer> deserializeOfflinePlayerList(ConfigurationSection section, String path) {
        List<OfflinePlayer> offlinePlayerList = new ArrayList<>();
        section.getStringList(path).forEach(string -> offlinePlayerList
                .add(Bukkit.getOfflinePlayer(UUID.fromString(string))));
        return offlinePlayerList;
    }
}
