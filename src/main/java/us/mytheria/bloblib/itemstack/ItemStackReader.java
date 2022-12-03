package us.mytheria.bloblib.itemstack;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.List;

public class ItemStackReader {

    public static ItemStackBuilder read(ConfigurationSection section) {
        String inputMaterial = section.getString("Material", "DIRT");
        Material material = Material.getMaterial(inputMaterial);
        if (material == null) {
            Bukkit.getLogger().severe("Material " + inputMaterial + " is not a valid material. Using DIRT instead.");
            material = Material.DIRT;
        }
        ItemStackBuilder builder = ItemStackBuilder.build(material);
        if (section.contains("Amount")) {
            builder = builder.amount(section.getInt("Amount"));
        }
        if (section.contains("DisplayName")) {
            builder = builder.displayName(section.getString("DisplayName"));
        }
        if (section.contains("Lore")) {
            builder = builder.lore(section.getStringList("Lore"));
        }
        if (section.contains("Unbreakable")) {
            builder = builder.unbreakable(section.getBoolean("Unbreakable"));
        }
        if (section.contains("Color")) {
            builder = builder.color(parseColor(section.getString("Color")));
        }
        if (section.contains("Enchantments")) {
            List<String> enchantNames = section.getStringList("Enchantments");
            HashMap<Enchantment, Integer> enchantments = new HashMap<>();
            enchantNames.forEach(element -> {
                String[] split = element.split(",");
                if (split.length != 2) {
                    Bukkit.getLogger().severe("Invalid element inside 'Enchantments': " + element);
                    return;
                }
                String key = split[0];
                int level;
                try {
                    level = Integer.parseInt(split[1]);
                } catch (NumberFormatException e) {
                    Bukkit.getLogger().severe("Invalid level for " + key + " enchantment: " + split[1]);
                    return;
                }
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(key));
                if (enchantment != null) {
                    enchantments.put(enchantment, level);
                } else {
                    Bukkit.getLogger().severe("Enchantment " + key + " is not a valid enchantment. Skipping.");
                }
            });
            builder = builder.enchant(enchantments);
        }
        return builder;
    }

    public static Color parseColor(String color) {
        String[] input = color.split(",");
        if (input.length != 3) {
            throw new IllegalArgumentException("Color " + color + " is not a valid color.");
        }
        try {
            int r = Integer.parseInt(input[0]);
            int g = Integer.parseInt(input[1]);
            int b = Integer.parseInt(input[2]);
            return Color.fromRGB(r, g, b);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Color " + color + " is not a valid color.");
        }
    }

    public static String parse(Color color) {
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }
}
