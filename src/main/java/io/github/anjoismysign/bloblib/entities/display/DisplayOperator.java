package io.github.anjoismysign.bloblib.entities.display;

import io.github.anjoismysign.bloblib.entities.BukkitPluginOperator;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an object that can hold Display entities
 * using initial displayData and a transformation.
 */
public interface DisplayOperator extends BukkitPluginOperator {

    /**
     * The initial DisplayData held by this DisplayOperator.
     *
     * @return The initial DisplayData held by this DisplayOperator.
     */
    DisplayData getDisplayData();

    /**
     * The initial transformation held by this DisplayOperator.
     *
     * @return The initial transformation held by this DisplayOperator.
     */
    Transformation getTransformation();

    /**
     * Applies the DisplayData and Transformation to the given Display.
     *
     * @param display the Display to apply the DisplayData and Transformation to
     */
    default void apply(@NotNull Display display) {
        if (getDisplayData() != null)
            getDisplayData().apply(display);
        if (getTransformation() != null)
            display.setTransformation(getTransformation());
    }
}
