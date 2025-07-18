package io.github.anjoismysign.bloblib.managers.fillermanager;

import io.github.anjoismysign.bloblib.entities.VariableFiller;
import io.github.anjoismysign.bloblib.entities.VariableValue;
import io.github.anjoismysign.bloblib.utilities.MaterialUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MaterialFiller implements VariableFiller<Material> {
    private final ArrayList<Material> materials;
    private final HashMap<Material, Material> nonItem;

    public MaterialFiller() {
        nonItem = new HashMap<>();
        materials = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.name().contains("LEGACY"))
                continue;
            if (!material.isItem())
                continue;
            materials.add(material);
        }
        MaterialUtil.fillNonItem(nonItem);
    }

    @Override
    public List<VariableValue<Material>> page(int page, int itemsPerPage) {
        int start = (page - 1) * itemsPerPage;
        int end = start + (itemsPerPage);
        ArrayList<VariableValue<Material>> values = new ArrayList<>();
        for (int i = start; i < end; i++) {
            Material material;
            try {
                material = materials.get(i);
                ItemStack itemStack;
                if (material.isItem())
                    itemStack = new ItemStack(material);
                else
                    itemStack = new ItemStack(nonItem.get(material));
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ChatColor.GOLD + material.name());
                itemStack.setItemMeta(itemMeta);
                values.add(new VariableValue<>(itemStack, material));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        return values;
    }

    @Override
    public int totalPages(int itemsPerPage) {
        return (int) Math.ceil((double) materials.size() / (double) itemsPerPage);
    }
}
