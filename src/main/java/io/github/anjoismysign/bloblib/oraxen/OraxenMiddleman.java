package io.github.anjoismysign.bloblib.oraxen;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface OraxenMiddleman {

    static OraxenMiddleman getInstance(){
        boolean isEnabled = Bukkit.getPluginManager().isPluginEnabled("Oraxen");
        if (isEnabled){
            return new OraxenFound();
        } else {
            return new OraxenNotFound();
        }
    }

    @NotNull
    ItemStack buildItem(@NotNull String id);

}
