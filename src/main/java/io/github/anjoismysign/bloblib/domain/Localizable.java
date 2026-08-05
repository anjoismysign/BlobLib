package io.github.anjoismysign.bloblib.domain;

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
