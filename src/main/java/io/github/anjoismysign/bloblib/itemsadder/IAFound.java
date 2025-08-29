package io.github.anjoismysign.bloblib.itemsadder;

import dev.lone.itemsadder.api.CustomStack;
import io.github.anjoismysign.bloblib.itemstack.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IAFound implements ItemsAdderMiddleman {
    @Override
    public @NotNull ItemStack itemStackOfCustomStack(@NotNull String namespacedId) {
        @Nullable CustomStack customStack = CustomStack.getInstance(namespacedId);
        if (customStack == null){
            return ItemStackBuilder.build(Material.POISONOUS_POTATO).itemName("&4[MISSING]&cItemsAdder{"+namespacedId+"}").build();
        }
        return customStack.getItemStack();
    }
}
