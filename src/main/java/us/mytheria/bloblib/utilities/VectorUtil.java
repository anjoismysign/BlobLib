package us.mytheria.bloblib.utilities;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

public class VectorUtil {

    public static Vector fromConfigurationSection(ConfigurationSection section) {
        return new Vector(section.getDouble("X", 0),
                section.getDouble("Y", 0),
                section.getDouble("Z", 0));
    }

    public static void setVector(Vector vector, ConfigurationSection section) {
        section.set("X", vector.getX());
        section.set("Y", vector.getY());
        section.set("Z", vector.getZ());
    }

    public static void absoluteVector(Vector vector) {
        vector.setX(Math.abs(vector.getX()));
        vector.setY(Math.abs(vector.getY()));
        vector.setZ(Math.abs(vector.getZ()));
    }

    public static Vector rotateVector(Vector vector, int degree) {
        switch (degree) {
            case 270 -> {
                return new Vector(IntegerUtil.invertSign(vector.getBlockZ()), vector.getBlockY(), vector.getBlockX());
            }
            case 180 -> {
                return new Vector(IntegerUtil.invertSign(vector.getBlockX()), vector.getBlockY(), IntegerUtil.invertSign(vector.getBlockZ()));
            }
            case 90 -> {
                return new Vector(vector.getBlockZ(), vector.getBlockY(), IntegerUtil.invertSign(vector.getBlockX()));
            }
            default -> {
                Bukkit.getLogger().info("Invalid rotation degree");
                return null;
            }
        }
    }
}
