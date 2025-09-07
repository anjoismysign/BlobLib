package io.github.anjoismysign.bloblib.middleman.itemsadder;

import io.github.anjoismysign.bloblib.middleman.itemstack.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class IANotFound implements ItemsAdderMiddleman {
    @Override
    public @NotNull ItemStack itemStackOfCustomStack(@NotNull String namespacedId) {
        return ItemStackBuilder.build(Material.POISONOUS_POTATO).itemName("&cItemsAdder{"+namespacedId+"}").build();
    }
}
