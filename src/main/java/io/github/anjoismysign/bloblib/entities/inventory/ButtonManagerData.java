package io.github.anjoismysign.bloblib.entities.inventory;

import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Map;

public record ButtonManagerData<T extends InventoryButton>(Map<String, T> inventoryButtons,
                                                           Map<Integer, ItemStack> itemStackMap) {
    public static <T extends InventoryButton> ButtonManagerData<T> of(Map<String, T> inventoryButtons,
                                                                      Map<Integer, ItemStack> itemStackMap) {
        return new ButtonManagerData<>(Collections.unmodifiableMap(inventoryButtons),
                Collections.unmodifiableMap(itemStackMap));
    }
}
