package io.github.anjoismysign.bloblib.weaponmechanics;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.events.BlobLibPreReloadEvent;
import io.github.anjoismysign.bloblib.events.BlobLibReloadEvent;
import me.deecaad.core.file.Configuration;
import me.deecaad.weaponmechanics.WeaponMechanics;
import me.deecaad.weaponmechanics.WeaponMechanicsAPI;
import me.deecaad.weaponmechanics.weapon.projectile.weaponprojectile.Projectile;
import me.deecaad.weaponmechanics.weapon.projectile.weaponprojectile.ProjectileSettings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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

    public boolean isEnabled(){
        return true;
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

    @Nullable
    public Double getSpread(@NotNull String weaponTitle){
        Configuration weaponConfigurations = WeaponMechanics.getInstance().getWeaponConfigurations();
        String path = weaponTitle + ".Shoot.Spread.Base_Spread";
        if (!weaponConfigurations.hasDouble(path)){
            return null;
        }
        return weaponConfigurations.getDouble(path);
    }

    @Nullable
    public Integer getDelayBetweenShots(@NotNull String weaponTitle){
        Configuration weaponConfigurations = WeaponMechanics.getInstance().getWeaponConfigurations();
        String path = weaponTitle + ".Shoot.Delay_Between_Shots";
        if (!weaponConfigurations.hasInt(path)){
            return null;
        }
        return weaponConfigurations.getInt(path);
    }

    @Nullable
    public Integer getFullyAutomaticShotsPerSecond(@NotNull String weaponTitle){
        Configuration weaponConfigurations = WeaponMechanics.getInstance().getWeaponConfigurations();
        String path = weaponTitle + ".Shoot.Fully_Automatic_Shots_Per_Second";
        if (!weaponConfigurations.hasInt(path)){
            return null;
        }
        return weaponConfigurations.getInt(path);
    }

    @Nullable
    public ProjectileSettings getProjectileSettings(@NotNull String weaponTitle){
        Configuration weaponConfigurations = WeaponMechanics.getInstance().getWeaponConfigurations();
        @Nullable Projectile projectile = weaponConfigurations.getObject(weaponTitle + ".Projectile", Projectile.class);
        if (projectile == null) {
            return null;
        }

        World world = Bukkit.getWorlds().getFirst();

        ProjectileSettings settings = projectile.create(null, new Location(world, 0,0,0), new Vector(0,0,0), null, null, null).getProjectileSettings();
        return new ProjectileSettings() {
            @Override
            public double getMaximumTravelDistance() {
                return settings.getMaximumTravelDistance();
            }
        };
    }

    @Nullable
    public Integer getMagazineSize(@NotNull String weaponTitle){
        Configuration weaponConfigurations = WeaponMechanics.getInstance().getWeaponConfigurations();
        String path = weaponTitle + ".Reload.Magazine_Size";
        if (!weaponConfigurations.hasInt(path)){
            return null;
        }
        return weaponConfigurations.getInt(path);
    }

    @Nullable
    public Integer getAmmoPerReload(@NotNull String weaponTitle){
        Configuration weaponConfigurations = WeaponMechanics.getInstance().getWeaponConfigurations();
        String path = weaponTitle + ".Reload.Ammo_Per_Reload";
        if (!weaponConfigurations.hasInt(path)){
            return null;
        }
        return weaponConfigurations.getInt(path);
    }

    @Nullable
    public Integer getReloadDuration(@NotNull String weaponTitle){
        Configuration weaponConfigurations = WeaponMechanics.getInstance().getWeaponConfigurations();
        String path = weaponTitle + ".Reload.Reload_Duration";
        if (!weaponConfigurations.hasInt(path)){
            return null;
        }
        return weaponConfigurations.getInt(path);
    }
}
