package us.mytheria.bloblib.itemstack;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Objects;

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
            builder = builder.deserializeAndEnchant(enchantNames);
        }
        if (section.contains("CustomModelData")) {
            builder = builder.customModelData(section.getInt("CustomModelData"));
        }
        boolean hideAll = section.getBoolean("HideAllItemFlags", false);
        if (hideAll)
            builder = builder.hideAll();
        if (section.contains("ItemFlags")) {
            List<String> flagNames = section.getStringList("ItemFlags");
            builder = builder.deserializeAndFlag(flagNames);
        }
        return builder;
    }

    public static ItemStackBuilder read(File file, String path) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return read(Objects.requireNonNull(config.getConfigurationSection(path)));
    }

    public static ItemStackBuilder read(File file) {
        return read(file, "ItemStack");
    }

    public static ItemStackBuilder read(YamlConfiguration config, String path) {
        return read(Objects.requireNonNull(config.getConfigurationSection(path)));
    }

    public static ItemStackBuilder read(YamlConfiguration config) {
        return read(config, "ItemStack");
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
