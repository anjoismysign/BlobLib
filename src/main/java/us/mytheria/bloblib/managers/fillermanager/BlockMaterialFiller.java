package us.mytheria.bloblib.managers.fillermanager;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mytheria.bloblib.entities.VariableFiller;
import us.mytheria.bloblib.entities.VariableValue;

import java.util.ArrayList;

public class BlockMaterialFiller implements VariableFiller {
    private ArrayList<Material> materials;

    public BlockMaterialFiller() {
        materials = new ArrayList<>();
        for (Material material : Material.values()) {
            if (!material.name().contains("LEGACY_"))
                continue;
            if (!material.isBlock())
                continue;
            materials.add(material);
        }
    }

    @Override
    public VariableValue[] page(int page, int itemsPerPage) {
        int start = (page - 1) * itemsPerPage;
        int end = start + (itemsPerPage - 1);
        ArrayList<VariableValue> values = new ArrayList<>();
        for (int i = start; i < end; i++) {
            Material material;
            try {
                material = materials.get(i);
                ItemStack itemStack = new ItemStack(material);
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
        return (int) Math.ceil(materials.size() / itemsPerPage);
    }
}
