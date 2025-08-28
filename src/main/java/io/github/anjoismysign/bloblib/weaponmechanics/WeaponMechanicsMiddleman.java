package io.github.anjoismysign.bloblib.weaponmechanics;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface WeaponMechanicsMiddleman {

    static WeaponMechanicsMiddleman getInstance(){
        boolean isEnabled = Bukkit.getPluginManager().isPluginEnabled("WeaponMechanics");
        if (isEnabled){
            return new WMFound();
        } else {
            return new WMNotFound();
        }
    }

    @NotNull
    Set<String> getWeaponTitles();

    @NotNull
    ItemStack generateWeapon(@NotNull String weaponTitle);

}
