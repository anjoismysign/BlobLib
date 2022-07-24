package us.mytheria.bloblib.utilities;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;

public class ItemStackUtil {

    @Nullable
    public static String serialize(ItemStack itemStack) {
        if (itemStack == null)
            return "null";
        if (!itemStack.hasItemMeta()) {
            Bukkit.getLogger().info("No ItemMeta found for " + itemStack.getType());
            return "null";
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        String customModelData = (itemMeta.hasCustomModelData() ? String.valueOf(itemMeta.getCustomModelData()) : "0");
        String toReturn = itemMeta.getDisplayName() + "%bis:%" + StringUtil.listStringCompactor(itemMeta.getLore()) + "%bis:%" + customModelData + "%bis:%" + itemStack.getType();
        return toReturn;
    }

    public static ItemStack deserialize(String serialized) {
        if (serialized.equals("null"))
            return null;
        String[] split = serialized.split("%bis:%");
        ItemStack itemStack = new ItemStack(Material.valueOf(split[3]));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(split[0]);
        itemMeta.setLore(StringUtil.listStringDecompactor(split[1]));
        itemMeta.setCustomModelData(Integer.parseInt(split[2]));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
