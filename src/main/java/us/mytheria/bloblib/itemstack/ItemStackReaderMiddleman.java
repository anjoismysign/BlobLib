package us.mytheria.bloblib.itemstack;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemStackReaderMiddleman {
    @NotNull
    ItemStack readOrFailFast(@NotNull ConfigurationSection section);

    @Nullable
    ItemStack attempRead(@NotNull ConfigurationSection section);
}
