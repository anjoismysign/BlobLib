package us.mytheria.bloblib.utilities;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class PlayerUtil {

    public static void giveItemToInventoryOrDrop(Player player, ItemStack item) {
        HashMap<Integer, ItemStack> left = player.getInventory().addItem(item);
        left.values().forEach(itemstack -> player.getWorld().dropItemNaturally(player.getLocation(), itemstack));
    }
}
