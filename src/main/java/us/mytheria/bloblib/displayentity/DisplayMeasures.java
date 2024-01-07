package us.mytheria.bloblib.displayentity;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * The idea behind DisplayMeasures is being able to resize Display entities
 * while automatically adjusting their translation so the Display entity
 * is centered.
 *
 * @param scaleX  The scale on the X axis.
 * @param scaleY  The scale on the Y axis.
 * @param scaleZ  The scale on the Z axis.
 * @param yOffset The Y offset.
 */
public record DisplayMeasures(float scaleX, float scaleY, float scaleZ,
                              float yOffset) {
    private static final DisplayMeasures DEFAULT_MEASURES =
            new DisplayMeasures(1, 1, 1, 0);

    /**
     * Will return a new DisplayMeasures with the given values
     * with no Y-Offset.
     *
     * @param scaleX The scale on the X axis.
     * @param scaleY The scale on the Y axis.
     * @param scaleZ The scale on the Z axis.
     * @return A new DisplayMeasures with the given values.
     */
    public static DisplayMeasures of(float scaleX, float scaleY, float scaleZ) {
        return new DisplayMeasures(scaleX, scaleY, scaleZ, 0);
    }

    /**
     * Will return the default DisplayMeasures.
     * Scale-X: 1.0
     * Scale-Y: 1.0
     * Scale-Z: 1.0
     * Y-Offset: 0.0
     *
     * @return The default DisplayMeasures.
     */
    public static DisplayMeasures DEFAULT() {
        return DEFAULT_MEASURES;
    }

    /**
     * Will read the DisplayMeasures from the given ConfigurationSection.
     *
     * @param section The ConfigurationSection to read from.
     * @return The DisplayMeasures read from the ConfigurationSection.
     */
    @NotNull
    public static DisplayMeasures READ_OR_FAIL_FAST(ConfigurationSection section) {
        return new DisplayMeasures((float) section.getDouble("Scale-X", 1),
                (float) section.getDouble("Scale-Y", 1),
                (float) section.getDouble("Scale-Z", 1),
                (float) section.getDouble("Y-Offset", 0));
    }

    /**
     * Will read the DisplayMeasures from the given ConfigurationSection.
     * If any exception is thrown, null will be returned.
     *
     * @param section The ConfigurationSection to read from.
     * @return The DisplayMeasures read from the ConfigurationSection,
     * null if any exception is thrown.
     */
    @Nullable
    public static DisplayMeasures READ_OR_NULL(ConfigurationSection section) {
        DisplayMeasures displayMeasures;
        try {
            displayMeasures = READ_OR_FAIL_FAST(section);
            return displayMeasures;
        } catch (Exception e) {
            return null;
        }
    }

    public Vector3f getScale() {
        return new Vector3f(scaleX, scaleY, scaleZ);
    }

    public Vector3f getTranslation() {
        return new Vector3f(0, yOffset, 0);
    }

    /**
     * Will merge the given Transformation with this DisplayMeasures.
     * This allows using existing LeftRotation & RightRotation Quaternions
     *
     * @param transformation The Transformation to merge with.
     * @return A new Transformation with the merged values.
     */
    public Transformation merge(Transformation transformation) {
        return new Transformation(getTranslation(),
                transformation.getLeftRotation(),
                getScale(),
                transformation.getRightRotation());
    }

    /**
     * Will return a new Transformation with the values of this DisplayMeasures.
     *
     * @return A new Transformation with the values of this DisplayMeasures.
     */
    public Transformation toTransformation() {
        return new Transformation(getTranslation(),
                new Quaternionf(0, 0, 0, 1),
                getScale(),
                new Quaternionf(0, 0, 0, 1));
    }

    /**
     * Will serialize this DisplayMeasures to the given ConfigurationSection.
     *
     * @param section The ConfigurationSection to serialize to.
     */
    public void serialize(ConfigurationSection section) {
        section.set("Scale-X", scaleX);
        section.set("Scale-Y", scaleY);
        section.set("Scale-Z", scaleZ);
        section.set("Y-Offset", yOffset);
    }
}
