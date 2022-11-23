package us.mytheria.bloblib.objects;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mytheria.bloblib.SkullCreator;

import java.util.ArrayList;
import java.util.List;

public class SerializableItem {
    public static ItemStack fromConfigurationSection(ConfigurationSection section) {
        ItemStack itemStack;
        String material;
        if (section != null)
            material = section.getString("Material", "HEAD-https://textures.minecraft.net/texture/" +
                    "65b95da1281642daa5d022adbd3e7cb69dc0942c81cd63be9c3857d222e1c8d9");
        else
            material = "HEAD-https://textures.minecraft.net/texture/" +
                    "65b95da1281642daa5d022adbd3e7cb69dc0942c81cd63be9c3857d222e1c8d9";
        if (material.contains("HEAD-")) {
            itemStack = SkullCreator.itemFromUrl(material.replace("HEAD-", ""));
        } else {
            itemStack = new ItemStack(Material.valueOf(material));
        }
        String displayName;
        if (section != null)
            displayName = ChatColor.translateAlternateColorCodes('&', section.getString("DisplayName", "NULL"));
        else
            displayName = "NULL";
        List<String> lore;
        if (section != null)
            lore = getLore(section, "Lore");
        else {
            lore = new ArrayList<>();
            lore.add("Please check your configuration");
            lore.add("it seems you are missing something");
        }
        int customModelData;
        if (section != null)
            customModelData = section.getInt("CustomModelData", 0);
        else
            customModelData = 0;
        if (itemStack.getType() == Material.AIR)
            return itemStack;
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        if (customModelData != 0)
            itemMeta.setCustomModelData(customModelData);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static List<String> getLore(ConfigurationSection section, String path) {
        List<String> lore = new ArrayList<>();
        for (String s : section.getStringList(path)) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return lore;
    }
}
