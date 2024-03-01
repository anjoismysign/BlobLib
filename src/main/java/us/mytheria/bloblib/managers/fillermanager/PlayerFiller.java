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

public class PlayerFiller implements VariableFiller<UUID> {

    public PlayerFiller() {
    }

    @Override
    public List<VariableValue<UUID>> page(int page, int itemsPerPage) {
        int start = (page - 1) * itemsPerPage;
        int end = start + (itemsPerPage - 1);
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
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

    @Override
    public int totalPages(int itemsPerPage) {
        return (int) Math.ceil((double) Bukkit.getOnlinePlayers().size() / (double) itemsPerPage);
    }
}
