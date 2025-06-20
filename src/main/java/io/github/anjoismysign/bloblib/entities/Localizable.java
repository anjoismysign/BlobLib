package io.github.anjoismysign.bloblib.entities;

import org.jetbrains.annotations.NotNull;

public interface Localizable {
    /**
     * Will retrieve the locale of the asset.
     *
     * @return The locale of the asset
     */
    @NotNull
    String locale();
}
