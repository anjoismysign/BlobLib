package us.mytheria.bloblib.entities.translatable;

import us.mytheria.bloblib.entities.positionable.Positionable;

public class TranslatablePositionableModder extends BlobTranslatableModder<TranslatablePositionable, Positionable> {

    /**
     * Will create a new instance of BlobTranslatableModder.
     *
     * @param translatable The translatable to modify
     * @return The BlobTranslatableModder
     */
    public static TranslatablePositionableModder mod(TranslatablePositionable translatable) {
        TranslatablePositionableModder translatableModder = new TranslatablePositionableModder();
        translatableModder.translatable = translatable;
        return translatableModder;
    }
}
