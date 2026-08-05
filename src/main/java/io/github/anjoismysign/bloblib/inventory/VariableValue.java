package io.github.anjoismysign.bloblib.inventory;

import org.bukkit.inventory.ItemStack;

public record VariableValue<T>(ItemStack itemStack, T value) {
}
