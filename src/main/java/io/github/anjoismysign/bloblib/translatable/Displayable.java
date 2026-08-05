package io.github.anjoismysign.bloblib.translatable;

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
