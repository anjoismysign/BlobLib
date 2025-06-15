package io.github.anjoismysign.bloblib.entities.translatable;

import org.bukkit.inventory.ItemStack;

public class TranslatableItemModder extends BlobTranslatableModder<TranslatableItem, ItemStack> {

    /**
     * Will create a new instance of BlobTranslatableModder.
     *
     * @param translatable The translatable to modify
     * @return The BlobTranslatableModder
     */
    public static TranslatableItemModder mod(TranslatableItem translatable) {
        TranslatableItemModder translatableModder = new TranslatableItemModder();
        translatableModder.translatable = translatable;
        return translatableModder;
    }
}
