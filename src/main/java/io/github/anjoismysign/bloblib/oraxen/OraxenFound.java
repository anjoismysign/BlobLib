package io.github.anjoismysign.bloblib.oraxen;

import io.github.anjoismysign.bloblib.itemstack.ItemStackBuilder;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OraxenFound implements OraxenMiddleman {
    @Override
    public @NotNull ItemStack buildItem(@NotNull String id) {
        @Nullable ItemBuilder itemBuilder = OraxenItems.getItemById(id);
        if (itemBuilder == null){
            return ItemStackBuilder.build(Material.POISONOUS_POTATO).itemName("&4[MISSING]&cOraxen{"+id+"}").build();
        }
        return itemBuilder.build();
    }
}
