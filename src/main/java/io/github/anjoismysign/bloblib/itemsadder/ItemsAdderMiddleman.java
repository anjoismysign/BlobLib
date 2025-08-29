package io.github.anjoismysign.bloblib.itemsadder;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ItemsAdderMiddleman {

    static ItemsAdderMiddleman getInstance(){
        boolean isEnabled = Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
        if (isEnabled){
            return new IAFound();
        } else {
            return new IANotFound();
        }
    }

    @NotNull
    ItemStack itemStackOfCustomStack(@NotNull String namespacedId);

}
