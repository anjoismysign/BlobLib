package us.mytheria.bloblib.entities.display;

import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Objects;

public record DisplayInfo(@NotNull DisplayData getDisplayData,
                          @NotNull Transformation getTransformation) {

    /**
     * Gets a DisplayInfo instance from the given Display.
     *
     * @param display the Display to get the DisplayInfo from
     * @return the DisplayInfo from the given Display
     */
    public static DisplayInfo of(@NotNull Display display) {
        Objects.requireNonNull(display, "'display' cannot be null");
        Transformation transformation = display.getTransformation();
        Vector3f translation = transformation.getTranslation();
        Quaternionf leftRotation = transformation.getLeftRotation();
        Vector3f scale = transformation.getScale();
        Quaternionf rightRotation = transformation.getRightRotation();
        return new DisplayInfo(DisplayData.of(display),
                new Transformation(
                        new Vector3f(translation.x, translation.y, translation.z),
                        new Quaternionf(leftRotation.x, leftRotation.y, leftRotation.z, leftRotation.w),
                        new Vector3f(scale.x, scale.y, scale.z),
                        new Quaternionf(rightRotation.x, rightRotation.y, rightRotation.z, rightRotation.w)));
    }

    /**
     * Applies the DisplayInfo to the given Display.
     *
     * @param display the Display to apply the DisplayInfo to
     */
    public void apply(@NotNull Display display) {
        getDisplayData.apply(display);
        display.setTransformation(getTransformation());
    }
}
