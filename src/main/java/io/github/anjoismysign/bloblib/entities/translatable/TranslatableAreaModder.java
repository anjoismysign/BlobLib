package io.github.anjoismysign.bloblib.entities.translatable;

import io.github.anjoismysign.bloblib.entities.area.Area;

public class TranslatableAreaModder extends BlobTranslatableModder<TranslatableArea, Area> {

    /**
     * Will create a new instance of BlobTranslatableModder.
     *
     * @param translatable The translatable to modify
     * @return The BlobTranslatableModder
     */
    public static TranslatableAreaModder mod(TranslatableArea translatable) {
        TranslatableAreaModder translatableModder = new TranslatableAreaModder();
        translatableModder.translatable = translatable;
        return translatableModder;
    }
}
