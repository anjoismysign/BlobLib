package us.mytheria.bloblib.utilities;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

public class SerializationLib {

    public static String serialize(Color color) {
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }

    public static Color deserializeColor(String string) {
        String[] split = string.split(",");
        return Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    public static String serialize(Vector vector) {
        return vector.getX() + "," + vector.getY() + "," + vector.getZ();
    }

    public static Vector deserializeVector(String string) {
        String[] split = string.split(",");
        return new Vector(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
    }

    public static String serialize(BlockVector vector) {
        return vector.getBlockX() + "," + vector.getBlockY() + "," + vector.getBlockZ();
    }

    public static BlockVector deserializeBlockVector(String string) {
        String[] split = string.split(",");
        return new BlockVector(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    public static String serialize(OfflinePlayer player) {
        return player.getUniqueId().toString();
    }

    public static OfflinePlayer deserializeOfflinePlayer(String string) {
        return Bukkit.getOfflinePlayer(UUID.fromString(string));
    }

    public static String serialize(UUID uuid) {
        return uuid.toString();
    }

    public static UUID deserializeUUID(String string) {
        return UUID.fromString(string);
    }

    public static String serialize(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() +
                "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
    }

    public static Location deserializeLocation(String string) {
        String[] split = string.split(",");
        return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }

    public static String serialize(Block block) {
        return block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ();
    }

    public static Block deserializeBlock(String string) {
        String[] split = string.split(",");
        return Bukkit.getWorld(split[0]).getBlockAt(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
    }

    public static String serialize(BigInteger bigInteger) {
        return bigInteger.toString();
    }

    public static BigInteger deserializeBigInteger(String string) {
        return new BigInteger(string);
    }

    public static String serialize(BigDecimal bigDecimal) {
        return bigDecimal.toString();
    }

    public static BigDecimal deserializeBigDecimal(String string) {
        return new BigDecimal(string);
    }
}
