package us.mytheria.bloblib.itemstack;

import me.anjoismysign.anjo.entities.Uber;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import us.mytheria.bloblib.SkullCreator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemStackReader {

    public static ItemStackBuilder read(ConfigurationSection section) {
        String inputMaterial = section.getString("Material", "DIRT");
        ItemStackBuilder builder;
        if (!inputMaterial.startsWith("HEAD-")) {
            Material material = Material.getMaterial(inputMaterial);
            if (material == null) {
                Bukkit.getLogger().severe("Material " + inputMaterial + " is not a valid material. Using DIRT instead.");
                material = Material.DIRT;
            }
            builder = ItemStackBuilder.build(material);
        } else
            builder = ItemStackBuilder.build(SkullCreator.itemFromUrl(inputMaterial.substring(5)));
        if (section.isInt("Amount")) {
            builder = builder.amount(section.getInt("Amount"));
        }
        if (section.isString("DisplayName")) {
            builder = builder.displayName(ChatColor
                    .translateAlternateColorCodes('&', section
                            .getString("DisplayName")));
        }
        if (section.isList("Lore")) {
            List<String> input = section.getStringList("Lore");
            List<String> lore = new ArrayList<>();
            input.forEach(string -> lore.add(ChatColor
                    .translateAlternateColorCodes('&', string)));
            builder = builder.lore(lore);
        }
        if (section.isBoolean("Unbreakable")) {
            builder = builder.unbreakable(section.getBoolean("Unbreakable"));
        }
        if (section.isString("Color")) {
            builder = builder.color(parseColor(section.getString("Color")));
        }
        if (section.isList("Enchantments")) {
            List<String> enchantNames = section.getStringList("Enchantments");
            builder = builder.deserializeAndEnchant(enchantNames);
        }
        if (section.isInt("CustomModelData")) {
            builder = builder.customModelData(section.getInt("CustomModelData"));
        }
        boolean showAll = section.getBoolean("ShowAllItemFlags", false);
        if (showAll)
            builder = builder.showAll();
        if (section.isList("ItemFlags")) {
            List<String> flagNames = section.getStringList("ItemFlags");
            builder = builder.deserializeAndFlag(flagNames);
        }
        if (section.isConfigurationSection("Attributes")) {
            ConfigurationSection attributes = section.getConfigurationSection("Attributes");
            Uber<ItemStackBuilder> uber = Uber.drive(builder);
            attributes.getKeys(false).forEach(key -> {
                if (!attributes.isConfigurationSection(key)) {
                    Bukkit.getLogger().severe("Attribute " + key + " is not a valid attribute.");
                    return;
                }
                ConfigurationSection attributeSection = attributes.getConfigurationSection(key);
                try {
                    Attribute attribute = Attribute.valueOf(key);
                    if (!attributeSection.isDouble("Amount")) {
                        Bukkit.getLogger().severe("Attribute " + key + " is not a valid attribute.");
                        return;
                    }
                    double amount = attributeSection.getDouble("Amount");
                    if (!attributeSection.isString("Operation")) {
                        Bukkit.getLogger().severe("Attribute " + key + " is not a valid attribute.");
                        return;
                    }
                    AttributeModifier.Operation operation = AttributeModifier.Operation.valueOf(attributeSection.getString("Operation"));
                    uber.talk(uber.thanks().attribute(attribute, amount, operation));
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().severe("Attribute " + key + " is not a valid attribute.");
                }
            });
            builder = uber.thanks();
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
