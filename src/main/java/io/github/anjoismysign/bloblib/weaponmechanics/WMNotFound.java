package io.github.anjoismysign.bloblib.weaponmechanics;

import io.github.anjoismysign.bloblib.middleman.itemstack.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class WMNotFound implements WeaponMechanicsMiddleman {
    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public @NotNull Set<String> getWeaponTitles() {
        return Set.of();
    }

    @Override
    public @NotNull ItemStack generateWeapon(@NotNull String weaponTitle) {
        return ItemStackBuilder.build(Material.POISONOUS_POTATO).itemName("&cWeaponMechanics{"+weaponTitle+"}").build();
    }

    @Override
    public @Nullable String getWeaponTitle(@NotNull ItemStack item) {
        return null;
    }

    @Override
    public void shoot(@NotNull LivingEntity shooter, @NotNull String weaponTitle, @NotNull Vector direction) {
    }

    @Override
    public boolean isReloading(@NotNull LivingEntity entity) {
        return false;
    }
}
