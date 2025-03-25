package us.mytheria.bloblib.entities.translatable;

import org.jetbrains.annotations.NotNull;

public interface Displayable<T> extends Translatable<T> {

    /**
     * Gets the display of this Displayable
     *
     * @return The name
     */
    @NotNull
    String getDisplay();
}
