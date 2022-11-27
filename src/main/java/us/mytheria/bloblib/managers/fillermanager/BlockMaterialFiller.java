package us.mytheria.bloblib.managers.fillermanager;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mytheria.bloblib.entities.VariableFiller;
import us.mytheria.bloblib.entities.VariableValue;
import us.mytheria.bloblib.utilities.MaterialUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class BlockMaterialFiller implements VariableFiller {
    private ArrayList<Material> materials;
    private HashMap<Material, Material> nonItem;

    public BlockMaterialFiller() {
        nonItem = new HashMap<>();
        materials = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.name().contains("LEGACY"))
                continue;
            if (!material.isBlock())
                continue;
            if (new ItemStack(material).getItemMeta() == null)
                continue;
            materials.add(material);
        }
        MaterialUtil.fillNonItem(nonItem);
    }

    @Override
    public VariableValue[] page(int page, int itemsPerPage) {
        int start = (page - 1) * itemsPerPage;
        int end = start + (itemsPerPage);
        ArrayList<VariableValue> values = new ArrayList<>();
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
                values.add(new VariableValue(itemStack, material));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        return values.toArray(new VariableValue[0]);
    }

    @Override
    public int totalPages(int itemsPerPage) {
        return (int) Math.ceil((double) materials.size() / (double) itemsPerPage);
    }
}
