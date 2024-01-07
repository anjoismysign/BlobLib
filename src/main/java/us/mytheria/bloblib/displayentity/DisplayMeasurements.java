package us.mytheria.bloblib.displayentity;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * The idea behind DisplayMeasurements is being able to resize Display entities
 * while automatically adjusting their translation so the Display entity
 * is centered.
 *
 * @param scaleX  The scale on the X axis.
 * @param scaleY  The scale on the Y axis.
 * @param scaleZ  The scale on the Z axis.
 * @param yOffset The Y offset.
 */
public record DisplayMeasurements(float scaleX, float scaleY, float scaleZ,
                                  float yOffset) {
    private static final DisplayMeasurements DEFAULT_MEASUREMENTS =
            new DisplayMeasurements(1, 1, 1, 0);

    /**
     * Will return a new DisplayMeasurements with the given values
     * with no Y-Offset.
     *
     * @param scaleX The scale on the X axis.
     * @param scaleY The scale on the Y axis.
     * @param scaleZ The scale on the Z axis.
     * @return A new DisplayMeasurements with the given values.
     */
    public static DisplayMeasurements of(float scaleX, float scaleY, float scaleZ) {
        return new DisplayMeasurements(scaleX, scaleY, scaleZ, 0);
    }

    /**
     * Will return the default DisplayMeasurements.
     * Scale-X: 1.0
     * Scale-Y: 1.0
     * Scale-Z: 1.0
     * Y-Offset: 0.0
     *
     * @return The default DisplayMeasurements.
     */
    public static DisplayMeasurements DEFAULT() {
        return DEFAULT_MEASUREMENTS;
    }

    /**
     * Will read the DisplayMeasurements from the given ConfigurationSection.
     *
     * @param section The ConfigurationSection to read from.
     * @return The DisplayMeasurements read from the ConfigurationSection.
     */
    @NotNull
    public static DisplayMeasurements READ_OR_FAIL_FAST(ConfigurationSection section) {
        return new DisplayMeasurements((float) section.getDouble("Scale-X", 1),
                (float) section.getDouble("Scale-Y", 1),
                (float) section.getDouble("Scale-Z", 1),
                (float) section.getDouble("Y-Offset", 0));
    }

    /**
     * Will read the DisplayMeasurements from the given ConfigurationSection.
     * If any exception is thrown, null will be returned.
     *
     * @param section The ConfigurationSection to read from.
     * @return The DisplayMeasurements read from the ConfigurationSection,
     * null if any exception is thrown.
     */
    @Nullable
    public static DisplayMeasurements READ_OR_NULL(ConfigurationSection section) {
        DisplayMeasurements displayMeasurements;
        try {
            displayMeasurements = READ_OR_FAIL_FAST(section);
            return displayMeasurements;
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
     * Will merge the given Transformation with this DisplayMeasurements.
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
     * Will return a new Transformation with the values of this DisplayMeasurements.
     *
     * @return A new Transformation with the values of this DisplayMeasurements.
     */
    public Transformation toTransformation() {
        return new Transformation(getTranslation(),
                new Quaternionf(0, 0, 0, 1),
                getScale(),
                new Quaternionf(0, 0, 0, 1));
    }

    /**
     * Will serialize this DisplayMeasurements to the given ConfigurationSection.
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
