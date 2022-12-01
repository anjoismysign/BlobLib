package us.mytheria.bloblib.managers.fillermanager;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mytheria.bloblib.entities.VariableFiller;
import us.mytheria.bloblib.entities.VariableValue;

import java.util.ArrayList;
import java.util.List;

public class ItemMaterialFiller implements VariableFiller<Material> {
    private final ArrayList<Material> materials;

    public ItemMaterialFiller() {
        materials = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.name().contains("LEGACY"))
                continue;
            if (!material.isItem())
                continue;
            if (new ItemStack(material).getItemMeta() == null)
                continue;
            materials.add(material);
        }
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
                ItemStack itemStack = new ItemStack(material);
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
