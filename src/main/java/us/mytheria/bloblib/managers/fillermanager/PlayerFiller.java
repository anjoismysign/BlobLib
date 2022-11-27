package us.mytheria.bloblib.managers.fillermanager;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mytheria.bloblib.entities.VariableFiller;
import us.mytheria.bloblib.entities.VariableValue;

import java.util.ArrayList;

public class PlayerFiller implements VariableFiller {

    public PlayerFiller() {
    }

    @Override
    public VariableValue[] page(int page, int itemsPerPage) {
        int start = (page - 1) * itemsPerPage;
        int end = start + (itemsPerPage - 1);
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        ArrayList<VariableValue> values = new ArrayList<>();
        for (int i = start; i < end; i++) {
            Player player;
            try {
                player = players.get(i);
                ItemStack itemStack = new ItemStack(Material.LEATHER_CHESTPLATE);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ChatColor.GOLD + player.getName());
                itemStack.setItemMeta(itemMeta);
                values.add(new VariableValue(itemStack, player.getUniqueId()));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        return values.toArray(new VariableValue[0]);
    }

    @Override
    public int totalPages(int itemsPerPage) {
        return (int) Math.ceil((double) Bukkit.getOnlinePlayers().size() / (double) itemsPerPage);
    }
}
