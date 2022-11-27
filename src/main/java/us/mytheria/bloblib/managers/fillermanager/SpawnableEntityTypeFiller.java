package us.mytheria.bloblib.managers.fillermanager;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mytheria.bloblib.entities.VariableFiller;
import us.mytheria.bloblib.entities.VariableValue;

import java.util.ArrayList;

public class SpawnableEntityTypeFiller implements VariableFiller {
    private ArrayList<EntityType> entities;

    public SpawnableEntityTypeFiller() {
        entities = new ArrayList<>();
        for (EntityType entityType : EntityType.values()) {
            if (!entityType.isSpawnable())
                continue;
            entities.add(entityType);
        }
    }

    @Override
    public VariableValue[] page(int page, int itemsPerPage) {
        int start = (page - 1) * itemsPerPage;
        int end = start + (itemsPerPage - 1);
        ArrayList<VariableValue> values = new ArrayList<>();
        for (int i = start; i < end; i++) {
            EntityType entityType;
            try {
                entityType = entities.get(i);
                ItemStack itemStack = new ItemStack(Material.SPAWNER);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ChatColor.GOLD + entityType.name());
                itemStack.setItemMeta(itemMeta);
                values.add(new VariableValue(itemStack, entityType));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        return values.toArray(new VariableValue[0]);
    }

    @Override
    public int totalPages(int itemsPerPage) {
        return (int) Math.ceil((double) entities.size() / (double) itemsPerPage);
    }
}
