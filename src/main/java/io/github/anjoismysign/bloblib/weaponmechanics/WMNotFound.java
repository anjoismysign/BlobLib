package io.github.anjoismysign.bloblib.weaponmechanics;

import io.github.anjoismysign.bloblib.itemstack.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class WMNotFound implements WeaponMechanicsMiddleman {
    @Override
    public @NotNull Set<String> getWeaponTitles() {
        return Set.of();
    }

    @Override
    public @NotNull ItemStack generateWeapon(@NotNull String weaponTitle) {
        return ItemStackBuilder.build(Material.POISONOUS_POTATO).itemName("&cWeaponMechanics{"+weaponTitle+"}").build();
    }
}
