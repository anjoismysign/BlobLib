package us.mytheria.bloblib.managers.fillermanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mytheria.bloblib.entities.VariableFiller;
import us.mytheria.bloblib.entities.VariableValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author anjoismysign
 * A filler that fills all players except the one specified
 */
public record AnyOtherPlayerFiller(Player exclude) implements VariableFiller<UUID> {

    /**
     * @param page         The page to get
     * @param itemsPerPage The amount of items per page
     * @return The items for the page
     */
    @Override
    public List<VariableValue<UUID>> page(int page, int itemsPerPage) {
        int start = (page - 1) * itemsPerPage;
        int end = start + (itemsPerPage - 1);
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        players.forEach(player -> {
            if (player.getUniqueId().equals(exclude.getUniqueId())) {
                players.remove(player);
            }
        });
        ArrayList<VariableValue<UUID>> values = new ArrayList<>();
        for (int i = start; i < end; i++) {
            Player player;
            try {
                player = players.get(i);
                ItemStack itemStack = new ItemStack(Material.LEATHER_CHESTPLATE);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ChatColor.GOLD + player.getName());
                itemStack.setItemMeta(itemMeta);
                values.add(new VariableValue<>(itemStack, player.getUniqueId()));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        return values;
    }

    /**
     * @param itemsPerPage The amount of items per page
     * @return The total amount of pages
     */
    @Override
    public int totalPages(int itemsPerPage) {
        return (int) Math.ceil((double) Bukkit.getOnlinePlayers().size() - 1 / (double) itemsPerPage);
    }
}
