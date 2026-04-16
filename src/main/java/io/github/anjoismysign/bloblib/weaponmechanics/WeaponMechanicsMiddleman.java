package io.github.anjoismysign.bloblib.weaponmechanics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface WeaponMechanicsMiddleman {

    static WeaponMechanicsMiddleman getInstance(){
        boolean isEnabled = Bukkit.getPluginManager().isPluginEnabled("WeaponMechanics");
        if (isEnabled){
            return WMFound.INSTANCE;
        } else {
            return new WMNotFound();
        }
    }


    boolean isEnabled();

    /**
     * Gets all available weapon titles using the WeaponConfiguration.
     * @return The non-null set.
     */
    @NotNull
    Set<String> getWeaponTitles();

    /**
     * Returns an item corresponding to the given <code>weaponTitle</code>. The item will have a custom
     * name, lore, enchantments, flags, nbt data, etc.
     *
     * @param weaponTitle The non-null weapon-title of the weapon to generate.
     * @return The non-null weapon item.
     * @see me.deecaad.weaponmechanics.weapon.info.InfoHandler
     */
    @NotNull
    ItemStack generateWeapon(@NotNull String weaponTitle);

    /**
     * Returns the weapon-title associated with the given item. If the given item is not a
     * WeaponMechanics weapon, this method will return <code>null</code>.
     *
     * <p>
     * Note that a weapon-title is the config name of a weapon, and you can use a weapon-title to pull
     * values from config easily.
     *
     * @param item The non-null item to get the weapon title from.
     * @return The item's weapon title, or null.
     */
    @Nullable String getWeaponTitle(@NotNull ItemStack item);

    /**
     * Shorthand for an entity to shoot a weapon at the given target location.
     *
     * @param shooter The non-null entity to shoot the weapon.
     * @param weaponTitle The non-null weapon title to shoot.
     * @param target The non-null target location to shoot at.
     */
    default void shootAt(@NotNull LivingEntity shooter, @NotNull String weaponTitle, @NotNull Location target){
        shoot(shooter, weaponTitle, target.toVector().subtract(shooter.getEyeLocation().toVector()));
    }

    /**
     * Shorthand for an entity to shoot a weapon in the direction the entity is currently facing.
     *
     * @param shooter The non-null entity to shoot the weapon.
     * @param weaponTitle The non-null weapon title to shoot.
     */
    default void shootWithShooterDirection(@NotNull LivingEntity shooter, @NotNull String weaponTitle){
        shoot(shooter, weaponTitle, shooter.getLocation().getDirection());
    }
    /**
     * Shorthand for an entity to shoot a weapon in the given direction.
     *
     * @param shooter The non-null entity to shoot the weapon.
     * @param weaponTitle The non-null weapon title to shoot.
     * @param direction The non-null direction to shoot the weapon.
     */
    void shoot(@NotNull LivingEntity shooter, @NotNull String weaponTitle, @NotNull Vector direction);

    /**
     * Returns <code>true</code> if the given <code>entity</code> is reloading their weapon.
     *
     * @param entity The non-null living entity to check the reload state of.
     * @return <code>true</code> if the entity is reloading.
     */
    boolean isReloading(@NotNull LivingEntity entity);
}
