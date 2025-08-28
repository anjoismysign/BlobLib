package io.github.anjoismysign.bloblib.weaponmechanics;

import me.deecaad.weaponmechanics.WeaponMechanics;
import me.deecaad.weaponmechanics.WeaponMechanicsAPI;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class WMFound implements WeaponMechanicsMiddleman {
    @Override
    public @NotNull Set<String> getWeaponTitles() {
        return WeaponMechanics.getInstance().getWeaponConfigurations().keys(false);
    }

    @Override
    public @NotNull ItemStack generateWeapon(@NotNull String weaponTitle) {
        return WeaponMechanicsAPI.generateWeapon(weaponTitle);
    }
}
