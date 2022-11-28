package us.mytheria.bloblib.entities;

import org.bukkit.inventory.ItemStack;

public record VariableValue<E>(ItemStack itemStack, E value) {
}
