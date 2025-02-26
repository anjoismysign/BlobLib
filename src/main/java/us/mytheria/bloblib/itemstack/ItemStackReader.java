package us.mytheria.bloblib.itemstack;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.anjoismysign.anjo.entities.Uber;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.SkullCreator;
import us.mytheria.bloblib.exception.ConfigurationFieldException;
import us.mytheria.bloblib.utilities.TextColor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ItemStackReader {

    public static ItemStackBuilder READ_OR_FAIL_FAST(ConfigurationSection section) {
        if (!section.isString("Material"))
            throw new ConfigurationFieldException("'Material' field is missing or not a String");
        RegistryAccess registryAccess = RegistryAccess.registryAccess();
        String inputMaterial = section.getString("Material");
        ItemStackBuilder builder;
        if (!inputMaterial.startsWith("HEAD-")) {
            Material material = Material.getMaterial(inputMaterial);
            if (material == null)
                throw new ConfigurationFieldException("'Material' field is not a valid material");
            builder = ItemStackBuilder.build(material);
        } else
            builder = ItemStackBuilder.build(SkullCreator.itemFromUrl(inputMaterial.substring(5)));
        if (section.isInt("Amount")) {
            int amount = section.getInt("Amount");
            if (amount < 1 || amount > 127)
                throw new ConfigurationFieldException("'Amount' field is not a valid amount");
            builder = builder.amount(section.getInt("Amount"));
        }
        if (section.isInt("Damage")) {
            int damage = section.getInt("Damage");
            if (damage < 0)
                throw new ConfigurationFieldException("'Damage' field is not a valid damage value");
            builder = builder.damage(damage);
        }
        if (section.isInt("RepairCost")) {
            int repairCost = section.getInt("RepairCost");
            if (repairCost < 0)
                throw new ConfigurationFieldException("'RepairCost' field is not a valid repair cost value");
            builder = builder.repairCost(repairCost);
        }
        if (section.isConfigurationSection("ArmorTrim")) {
            ConfigurationSection armorTrim = section.getConfigurationSection("ArmorTrim");
            if (!armorTrim.isString("TrimMaterial"))
                throw new ConfigurationFieldException("ArmorTrim is missing 'TrimMaterial' field");
            String trimMaterial = armorTrim.getString("TrimMaterial");
            if (!armorTrim.isString("TrimPattern"))
                throw new ConfigurationFieldException("ArmorTrim is missing 'TrimPattern' field");
            String trimPattern = armorTrim.getString("TrimPattern");
            TrimMaterial material = registryAccess.getRegistry(RegistryKey.TRIM_MATERIAL).get(Key.key(trimMaterial));
            if (material == null)
                throw new ConfigurationFieldException("'" + trimPattern + "' is not a valid TrimMaterial");
            TrimPattern pattern = registryAccess.getRegistry(RegistryKey.TRIM_PATTERN).get(Key.key(trimPattern));
            if (pattern == null)
                throw new ConfigurationFieldException("'" + trimPattern + "' is not a valid TrimPattern");
            builder = builder.armorTrim(new ArmorTrim(material, pattern));
        }
        if (section.isString("ItemName")) {
            String itemName = section.getString("ItemName");
            builder = builder.itemName(TextColor.PARSE(itemName));
        }
        if (section.isBoolean("HideToolTip")) {
            boolean hideToolTip = section.getBoolean("HideToolTip");
            builder = builder.hideToolTip(hideToolTip);
        }
        if (section.isBoolean("EnchantmentGlintOverride")) {
            boolean enchantmentGlintOverride = section.getBoolean("EnchantmentGlintOverride");
            builder = builder.enchantmentGlintOverride(enchantmentGlintOverride);
        }
        if (section.isBoolean("FireResistant")) {
            boolean fireResistant = section.getBoolean("FireResistant");
            builder = builder.fireResistant(fireResistant);
        }
        if (section.isInt("MaxStackSize")) {
            int maxStackSize = section.getInt("MaxStackSize");
            builder = builder.maxStackSize(maxStackSize);
        }
        if (section.isInt("MaxDamage")) {
            int maxDamage = section.getInt("MaxDamage");
            if (maxDamage < 0)
                throw new ConfigurationFieldException("'MaxDamage' field is not a valid max damage value");
            builder = builder.maxDamage(maxDamage);
        }
        if (section.isString("Rarity")) {
            ItemRarity rarity = ItemRarity.valueOf(section.getString("Rarity"));
            builder = builder.rarity(rarity);
        }
        if (section.isConfigurationSection("Food")) {
            ConfigurationSection food = section.getConfigurationSection("Food");
            if (food.isInt("Nutrition"))
                builder = builder.food(foodComponent -> foodComponent.setNutrition(food.getInt("Nutrition")));
            if (food.isDouble("Saturation")) {
                float saturation = (float) food.getDouble("Saturation");
                builder = builder.food(foodComponent -> foodComponent.setSaturation(saturation));
            }
            if (food.isBoolean("CanAlwaysEat"))
                builder = builder.food(foodComponent -> foodComponent.setCanAlwaysEat(food.getBoolean("CanAlwaysEat")));
        }
        if (section.isString("DisplayName")) {
            builder = builder.displayName(TextColor.PARSE(section
                    .getString("DisplayName")));
        }
        if (section.isList("Lore")) {
            List<String> input = section.getStringList("Lore");
            List<String> lore = new ArrayList<>();
            input.forEach(string -> lore.add(TextColor.PARSE(string)));
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
        if (section.isConfigurationSection("Attributes")) {
            ConfigurationSection attributes = section.getConfigurationSection("Attributes");
            Uber<ItemStackBuilder> uber = Uber.drive(builder);
            attributes.getKeys(false).forEach(key -> {
                if (!attributes.isConfigurationSection(key))
                    throw new ConfigurationFieldException("Attribute '" + key + "' is not valid");
                ConfigurationSection attributeSection = attributes.getConfigurationSection(key);
                try {
                    String name = attributes.getString("Name", UUID.randomUUID().toString());
                    Attribute attribute = registryAccess.getRegistry(RegistryKey.ATTRIBUTE).get(Key.key(key));
                    if (!attributeSection.isDouble("Amount"))
                        throw new ConfigurationFieldException("Attribute '" + key + "' has an invalid amount (DECIMAL NUMBER)");
                    double amount = attributeSection.getDouble("Amount");
                    if (!attributeSection.isString("Operation"))
                        throw new ConfigurationFieldException("Attribute '" + key + "' is missing 'Operation' field");
                    EquipmentSlotGroup equipmentSlot;
                    String readEquipmentSlotGroup = attributeSection.getString("EquipmentSlotGroup");
                    if (readEquipmentSlotGroup != null) {
                        try {
                            equipmentSlot = EquipmentSlotGroup.getByName(readEquipmentSlotGroup);
                        } catch ( IllegalArgumentException exception ) {
                            throw new ConfigurationFieldException("EquipmentSlot '" + readEquipmentSlotGroup + "' is not valid");
                        }
                    } else
                        equipmentSlot = null;
                    AttributeModifier.Operation operation = AttributeModifier.Operation.valueOf(attributeSection.getString("Operation"));
                    uber.talk(uber.thanks().attribute(attribute, name, amount, operation, equipmentSlot));
                } catch ( IllegalArgumentException exception ) {
                    throw new ConfigurationFieldException("Attribute '" + key + "' has an invalid Operation");
                }
            });
            builder = uber.thanks();
        }
        builder.hideAll();
        boolean showAll = section.getBoolean("ShowAllItemFlags", false);
        if (showAll)
            builder = builder.showAll();
        if (section.isList("ItemFlags")) {
            List<String> flagNames = section.getStringList("ItemFlags");
            builder = builder.deserializeAndFlag(flagNames);
        }
        return builder;
    }

    @NotNull
    public static ItemStackBuilder read(ConfigurationSection section) {
        ItemStackBuilder builder;
        try {
            builder = READ_OR_FAIL_FAST(section);
            return builder;
        } catch ( Exception exception ) {
            Bukkit.getLogger().severe(exception.getMessage());
            return ItemStackBuilder.build(Material.DIRT);
        }
    }

    public static ItemStackBuilder read(File file, String path) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return READ_OR_FAIL_FAST(Objects.requireNonNull(config.getConfigurationSection(path)));
    }

    public static ItemStackBuilder read(File file) {
        return read(file, "ItemStack");
    }

    public static ItemStackBuilder read(YamlConfiguration config, String path) {
        return READ_OR_FAIL_FAST(Objects.requireNonNull(config.getConfigurationSection(path)));
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
        } catch ( NumberFormatException exception ) {
            throw new IllegalArgumentException("Color " + color + " is not a valid color.");
        }
    }

    public static String parse(Color color) {
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }
}
