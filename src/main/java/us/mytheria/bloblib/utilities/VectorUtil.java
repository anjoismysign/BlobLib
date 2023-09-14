package us.mytheria.bloblib.utilities;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class VectorUtil {

    public static Vector fromConfigurationSection(ConfigurationSection section) {
        return new Vector(section.getDouble("X", 0),
                section.getDouble("Y", 0),
                section.getDouble("Z", 0));
    }

    public static void toConfigurationSection(Vector vector, ConfigurationSection section) {
        section.set("X", vector.getX());
        section.set("Y", vector.getY());
        section.set("Z", vector.getZ());
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

    /**
     * Rotates a vector.
     *
     * @param vector the vector to rotate
     * @param degree the degree to rotate
     * @return the rotated vector
     */
    @NotNull
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
            case 0 -> {
                return vector;
            }
            default -> {
                throw new IllegalArgumentException("Degree must be 0, 90, 180, or 270, but was " + degree);
            }
        }
    }

    /**
     * Uses floating point math to rotate a vector.
     * Allows for more precise rotations.
     *
     * @param vector the vector to rotate
     * @param degree the degree to rotate
     * @return the rotated vector
     */
    @NotNull
    public static Vector floatRotateVector(Vector vector, int degree) {
        switch (degree) {
            case 270 -> {
                return new Vector(DoubleUtil.invertSign(vector.getZ()), vector.getY(), vector.getX());
            }
            case 180 -> {
                return new Vector(DoubleUtil.invertSign(vector.getX()), vector.getY(), DoubleUtil.invertSign(vector.getZ()));
            }
            case 90 -> {
                return new Vector(vector.getZ(), vector.getY(), DoubleUtil.invertSign(vector.getX()));
            }
            case 0 -> {
                return vector;
            }
            default -> {
                throw new IllegalArgumentException("Degree must be 0, 90, 180, or 270, but was " + degree);
            }
        }
    }
}
