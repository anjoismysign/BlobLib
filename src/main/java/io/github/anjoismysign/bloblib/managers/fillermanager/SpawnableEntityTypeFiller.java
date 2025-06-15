package io.github.anjoismysign.bloblib.managers.fillermanager;

import io.github.anjoismysign.bloblib.entities.VariableFiller;
import io.github.anjoismysign.bloblib.entities.VariableValue;
import io.github.anjoismysign.bloblib.utilities.MaterialUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpawnableEntityTypeFiller implements VariableFiller<EntityType> {
    private final ArrayList<EntityType> entities;
    private final HashMap<EntityType, Material> materials;

    public SpawnableEntityTypeFiller() {
        entities = new ArrayList<>();
        materials = new HashMap<>();
        for (EntityType entityType : EntityType.values()) {
            if (!entityType.isSpawnable())
                continue;
            entities.add(entityType);
        }
        MaterialUtil.fillEntityTypeMaterial(materials);
    }

    @Override
    public List<VariableValue<EntityType>> page(int page, int itemsPerPage) {
        int start = (page - 1) * itemsPerPage;
        int end = start + (itemsPerPage);
        ArrayList<VariableValue<EntityType>> values = new ArrayList<>();
        for (int i = start; i < end; i++) {
            EntityType entityType;
            try {
                entityType = entities.get(i);
                ItemStack itemStack = new ItemStack(materials.get(entityType));
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ChatColor.GOLD + entityType.name());
                itemStack.setItemMeta(itemMeta);
                values.add(new VariableValue<>(itemStack, entityType));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        return values;
    }

    @Override
    public int totalPages(int itemsPerPage) {
        return (int) Math.ceil((double) entities.size() / (double) itemsPerPage);
    }
}
