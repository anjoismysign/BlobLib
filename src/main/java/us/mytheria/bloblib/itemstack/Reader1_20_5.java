package us.mytheria.bloblib.itemstack;

import me.anjoismysign.itemstack.ItemStackReader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Reader1_20_5 implements ItemStackReaderMiddleman {
    private static Reader1_20_5 instance;

    public static Reader1_20_5 getInstance() {
        if (instance == null) {
            instance = new Reader1_20_5();
        }
        return instance;
    }

    @Override
    public @NotNull ItemStack readOrFailFast(@NotNull ConfigurationSection section) {
        return ItemStackReader.READ_OR_FAIL_FAST(section).build();
    }

    @Override
    public @Nullable ItemStack attempRead(@NotNull ConfigurationSection section) {
        try {
            return readOrFailFast(section);
        } catch (Throwable e) {
            return null;
        }
    }
}
