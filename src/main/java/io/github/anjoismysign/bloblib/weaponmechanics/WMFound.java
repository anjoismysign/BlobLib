package io.github.anjoismysign.bloblib.weaponmechanics;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.events.BlobLibPreReloadEvent;
import io.github.anjoismysign.bloblib.events.BlobLibReloadEvent;
import me.deecaad.weaponmechanics.WeaponMechanics;
import me.deecaad.weaponmechanics.WeaponMechanicsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public enum WMFound implements WeaponMechanicsMiddleman, Listener {
    INSTANCE;

    WMFound(){
        Bukkit.getPluginManager().registerEvents(this, BlobLib.getInstance());
    }

    @EventHandler
    public void onPreReload(BlobLibPreReloadEvent event){
        WeaponInfoDisplay.INSTANCE.preReload();
    }

    @EventHandler
    public void onReload(BlobLibReloadEvent event){
        WeaponInfoDisplay.INSTANCE.reload();
    }

    @Override
    public @NotNull Set<String> getWeaponTitles() {
        return WeaponMechanics.getInstance().getWeaponConfigurations().keys(false);
    }

    @Override
    public @NotNull ItemStack generateWeapon(@NotNull String weaponTitle) {
        return WeaponMechanicsAPI.generateWeapon(weaponTitle);
    }

    @Override
    public @Nullable String getWeaponTitle(@NotNull ItemStack item) {
        return WeaponMechanicsAPI.getWeaponTitle(item);
    }

    @Override
    public void shoot(@NotNull LivingEntity shooter, @NotNull String weaponTitle, @NotNull Vector direction) {
        WeaponMechanicsAPI.shoot(shooter, weaponTitle, direction);
    }

    @Override
    public boolean isReloading(@NotNull LivingEntity entity) {
        return WeaponMechanicsAPI.isReloading(entity);
    }
}
