package us.mytheria.bloblib.entities;

import org.bukkit.inventory.ItemStack;

public record VariableValue<T>(ItemStack itemStack, T value) {
}
