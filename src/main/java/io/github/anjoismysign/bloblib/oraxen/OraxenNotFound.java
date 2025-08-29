package io.github.anjoismysign.bloblib.oraxen;

import io.github.anjoismysign.bloblib.itemstack.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OraxenNotFound implements OraxenMiddleman {
    @Override
    public @NotNull ItemStack buildItem(@NotNull String id) {
        return ItemStackBuilder.build(Material.POISONOUS_POTATO).itemName("&cOraxen{"+id+"}").build();
    }
}
