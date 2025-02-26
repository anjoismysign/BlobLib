package us.mytheria.bloblib.itemapi;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ItemMaterial {

    @NotNull
    String getReference();

    @NotNull
    ItemStack toItemStack(@NotNull Player player);

    boolean isInstance(@NotNull ItemStack itemStack);

    default boolean isInstance(@NotNull String reference) {
        return getReference().equals(reference);
    }
}
