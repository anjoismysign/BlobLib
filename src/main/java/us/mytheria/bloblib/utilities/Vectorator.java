package us.mytheria.bloblib.utilities;

import org.bukkit.block.BlockFace;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.exception.CardinalDirectionException;

import java.util.Objects;

/**
 * A decorator for the Vector class
 *
 * @param vector
 */
public record Vectorator(Vector vector) {
    /**
     * Creates a new Vectorator from a Vector
     *
     * @param vector The vector to create the Vectorator from
     * @return The new Vectorator
     */
    @NotNull
    public static Vectorator of(@NotNull Vector vector) {
        Objects.requireNonNull(vector);
        return new Vectorator(vector);
    }

    /**
     * Creates a new Vectorator from an x, y, and z coordinate
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     * @return The new Vectorator
     */
    @NotNull
    public static Vectorator of(int x, int y, int z) {
        return new Vectorator(new Vector(x, y, z));
    }

    /**
     * Creates a new Vectorator from an x, y, and z coordinate
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     * @return The new Vectorator
     */
    @NotNull
    public static Vectorator of(double x, double y, double z) {
        return new Vectorator(new Vector(x, y, z));
    }

    /**
     * Will get the minimum of the two vectors
     *
     * @param compare The vector to compare to
     * @return The minimum of the two vectors
     */
    @NotNull
    public Vector minimum(Vector compare) {
        return Vector.getMinimum(this.vector, compare);
    }

    /**
     * Will get the maximum of the two vectors
     *
     * @param compare The vector to compare to
     * @return The maximum of the two vectors
     */
    @NotNull
    public Vector maximum(Vector compare) {
        return Vector.getMaximum(this.vector, compare);
    }

    /**
     * Will rotate the vector by the given degree
     *
     * @param degree The degree to rotate the vector by
     * @return The rotated vector
     */
    public Vector rotate(int degree) {
        if (degree == 0)
            return vector;
        return VectorUtil.rotateVector(vector, degree);
    }

    /**
     * Will rotate the vector by the given BlockFace
     * as a cardinal direction.
     *
     * @param face The BlockFace to rotate the vector by
     * @return The rotated vector
     */
    public Vector rotate(BlockFace face) {
        switch (face) {
            case NORTH -> {
                return rotate(0);
            }
            case EAST -> {
                return rotate(270);
            }
            case SOUTH -> {
                return rotate(180);
            }
            case WEST -> {
                return rotate(90);
            }
            default -> {
                throw new CardinalDirectionException("The BlockFace '" + face + "' is not a cardinal direction!");
            }
        }
    }

    /**
     * Will rotate the vector by the given StructureRotation
     *
     * @param rotation The StructureRotation to rotate the vector by
     * @return The rotated vector
     */
    public Vector rotate(StructureRotation rotation) {
        switch (rotation) {
            case NONE -> {
                return rotate(0);
            }
            case CLOCKWISE_90 -> {
                return rotate(270);
            }
            case CLOCKWISE_180 -> {
                return rotate(180);
            }
            case COUNTERCLOCKWISE_90 -> {
                return rotate(90);
            }
            default ->
                    throw new CardinalDirectionException("The StructureRotation '" + rotation + "' is not a cardinal direction!");
        }
    }
}
