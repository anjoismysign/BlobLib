package io.github.anjoismysign.bloblib.weaponmechanics;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.api.BlobLibMessageAPI;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableItem;
import io.github.anjoismysign.bloblib.utilities.ItemStackUtil;
import me.deecaad.weaponmechanics.utils.CustomTag;
import me.deecaad.weaponmechanics.weapon.weaponevents.WeaponReloadCompleteEvent;
import me.deecaad.weaponmechanics.weapon.weaponevents.WeaponReloadEvent;
import me.deecaad.weaponmechanics.weapon.weaponevents.WeaponShootEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum WeaponInfoDisplay implements Listener {
    INSTANCE;

    WeaponInfoDisplay(){
        reload();
    }

    public void map(@NotNull String weaponTitle,
                           @NotNull String translatableItem){
        map.put(weaponTitle, translatableItem);
    }

    private final Map<String, String> map = new HashMap<>();

    public void preReload(){
        map.clear();
    }

    public void reload(){
        BlobLib plugin = BlobLib.getInstance();
        HandlerList.unregisterAll(this);
        boolean enabled = plugin.getConfig().getBoolean("WeaponMechanics.Actionbar-Weapon-Info-Display", false);
        if (!enabled){
            return;
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onReload(WeaponReloadCompleteEvent event){
        LivingEntity shooter = event.getShooter();
        if (shooter.getType() != EntityType.PLAYER){
            return;
        }
        String weaponTitle = event.getWeaponTitle();
        @Nullable String identifier = map.get(weaponTitle);
        if (identifier == null){
            return;
        }
        @Nullable TranslatableItem item = TranslatableItem.by(identifier);
        if (item == null){
            return;
        }
        ItemStack weaponStack = event.getWeaponStack();
        CustomTag tag = CustomTag.AMMO_LEFT;
        int leftAmmo = tag.hasInteger(weaponStack) ?
                tag.getInteger(weaponStack): 0;

        Player player = (Player) shooter;
        String locale = player.getLocale();
        ItemStack itemStack = item.localize(locale).getClone();
        String title = ItemStackUtil.display(itemStack);
        BlobLibMessageAPI.getInstance()
                .getMessage("WeaponMechanics.Weapon-Info-Display", player)
                .modder()
                .replace("%name%", title)
                .replace("%ammo%", leftAmmo+"")
                .get()
                .handle(player);
    }

    @EventHandler
    public void onShoot(WeaponShootEvent event){
        LivingEntity shooter = event.getShooter();
        if (shooter.getType() != EntityType.PLAYER){
            return;
        }
        String weaponTitle = event.getWeaponTitle();
        @Nullable String identifier = map.get(weaponTitle);
        if (identifier == null){
            return;
        }
        @Nullable TranslatableItem item = TranslatableItem.by(identifier);
        if (item == null){
            return;
        }
        ItemStack weaponStack = event.getWeaponStack();
        CustomTag tag = CustomTag.AMMO_LEFT;
        int leftAmmo = tag.hasInteger(weaponStack) ?
                tag.getInteger(weaponStack): 0;

        Player player = (Player) shooter;
        String locale = player.getLocale();
        ItemStack itemStack = item.localize(locale).getClone();
        String title = ItemStackUtil.display(itemStack);
        BlobLibMessageAPI.getInstance()
                .getMessage("WeaponMechanics.Weapon-Info-Display", player)
                .modder()
                .replace("%name%", title)
                .replace("%ammo%", leftAmmo+"")
                .get()
                .handle(player);
    }

}
