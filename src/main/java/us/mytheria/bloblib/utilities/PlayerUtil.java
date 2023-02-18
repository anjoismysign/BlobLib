package us.mytheria.bloblib.utilities;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

    /**
     * Gets the block the player is currently targeting.
     *
     * @param player the player's who's targeted block is to be checked.
     * @param range  the maximum range to check for a target. Needs to be at least 100.
     * @return the targeted block.
     */
    public static Block getTargetBlock(Player player, int range) {
        if (range < 100)
            range = 100;
        return player.getTargetBlock(null, range);
    }

    /**
     * Gets the block the player is currently targeting.
     *
     * @param player the player's who's targeted block is to be checked.
     * @return the targeted block.
     */
    public static Block getTargetBlock(Player player) {
        return getTargetBlock(player, 100);
    }

    /**
     * Gets the BlockFace of the block the player is currently targeting.
     *
     * @param player the player's who's targeted blocks BlockFace is to be checked.
     * @return the BlockFace of the targeted block, or null if the targeted block is non-occluding.
     */
    public static BlockFace getBlockFace(Player player) {
        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 100);
        if (lastTwoTargetBlocks.size() != 2 || !lastTwoTargetBlocks.get(1).getType().isOccluding()) return null;
        Block targetBlock = lastTwoTargetBlocks.get(1);
        Block adjacentBlock = lastTwoTargetBlocks.get(0);
        return targetBlock.getFace(adjacentBlock);
    }

    /**
     * Gets the block adjacent to the block the player is currently targeting.
     *
     * @param player the player's who's targeted block is to be checked.
     * @return the adjacent block, or null if the targeted block is non-occluding.
     */
    public static Block getAdyacentBlock(Player player) {
        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 100);
        if (lastTwoTargetBlocks.size() != 2 || !lastTwoTargetBlocks.get(1).getType().isOccluding()) return null;
        return lastTwoTargetBlocks.get(0);
    }

    /**
     * Will break the block and play the sound of the block breaking.
     *
     * @param player the player who is breaking the block.
     * @param block  the block to be broken.
     */
    public static void breakBlockAndPlaySound(Player player, Block block) {
        BlockUtil.playBreakSound(block);
        block.breakNaturally(player.getInventory().getItemInMainHand());
    }
}
