package io.github.anjoismysign.bloblib.entities.translatable;

import java.util.List;

public class TranslatableBlockModder extends BlobTranslatableModder<TranslatableBlock, List<String>> {

    /**
     * Will create a new instance of BlockModder.
     *
     * @param translatable The translatable to modify
     * @return The BlobTranslatableModder
     */
    public static TranslatableBlockModder mod(TranslatableBlock translatable) {
        TranslatableBlockModder translatableModder = new TranslatableBlockModder();
        translatableModder.translatable = translatable;
        return translatableModder;
    }
}
