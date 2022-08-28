package us.mytheria.bloblib.utilities;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public class PlayerUtil {

    public static void giveItemToInventoryOrDrop(Player player, ItemStack item) {
        HashMap<Integer, ItemStack> left = player.getInventory().addItem(item);
        left.values().forEach(itemstack -> player.getWorld().dropItemNaturally(player.getLocation(), itemstack));
    }

    public static boolean sendMessages(CommandSender sender,
                                       List<String> messages,
                                       @Nullable String permission,
                                       @Nullable String permissionMessage) {
        if (permission == null) {
            for (String message : messages) {
                sender.sendMessage(message);
            }
            return true;
        } else {
            if (sender.hasPermission(permission)) {
                for (String message : messages) {
                    sender.sendMessage(message);
                }
                return true;
            } else {
                if (permissionMessage != null) {
                    sender.sendMessage(permissionMessage);
                }
                return true;
            }
        }
    }

    public static boolean sendMessages(CommandSender sender,
                                       List<String> messages) {
        return sendMessages(sender, messages, null, null);
    }
}
