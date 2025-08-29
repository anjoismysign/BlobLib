package io.github.anjoismysign.bloblib.itemstack;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.github.anjoismysign.anjo.entities.Uber;
import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import io.github.anjoismysign.bloblib.itemsadder.ItemsAdderMiddleman;
import io.github.anjoismysign.bloblib.utilities.TextColor;
import io.github.anjoismysign.bloblib.weaponmechanics.WeaponMechanicsMiddleman;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings({"PatternValidation", "UnstableApiUsage"})
public class ItemStackReader {

    public static PlayerProfile profile(@NotNull String url) {
        PlayerProfile profile = Bukkit.createProfile("Notch");
        PlayerTextures textures = profile.getTextures();
        URL skin = null;
        try {
            skin = URI.create(url).toURL();
        } catch (MalformedURLException exception) {
            throw new RuntimeException(exception);
        }
        textures.setSkin(skin);
        profile.setTextures(textures);
        return profile;
    }

    @Deprecated
    public static ItemStackBuilder READ_OR_FAIL_FAST(ConfigurationSection section){
        return ItemStackBuilder.build(OMNI_STACK(section).getCopy());
    }

    @NotNull
    public static OmniStack OMNI_STACK(ConfigurationSection section) {
        RegistryAccess registryAccess = RegistryAccess.registryAccess();
        @Nullable String inputMaterial = section.getString("Material");
        Objects.requireNonNull(inputMaterial, "'Material' is not set");
        final Supplier<ItemStack> stackSupplier;
        if (!inputMaterial.startsWith("HEAD-")
                && !inputMaterial.startsWith("WM-")
                && !inputMaterial.startsWith("IA-")) {
            Material material = Material.getMaterial(inputMaterial);
            if (material == null)
                throw new ConfigurationFieldException("'" + inputMaterial + "' is not a valid material");
            stackSupplier = () -> new ItemStack(material);
        } else if (inputMaterial.startsWith("HEAD-")) {
            ItemStackBuilder builder = ItemStackBuilder.build(Material.PLAYER_HEAD);
            String url = inputMaterial.substring(5);
            builder.playerProfile(profile(url));
            stackSupplier = builder::build;
        } else if (inputMaterial.startsWith("WM-")) {
            String weaponTitle = inputMaterial.substring(3);
            stackSupplier = () -> WeaponMechanicsMiddleman.getInstance().generateWeapon(weaponTitle);
        } else {
            String namespacedId = inputMaterial.substring(3);
            stackSupplier = () -> ItemsAdderMiddleman.getInstance().itemStackOfCustomStack(namespacedId);
        }
        final Consumer<ItemStackBuilder> builderConsumer = builder -> {
            if (section.isInt("Amount")) {
                int amount = section.getInt("Amount");
                if (amount < 1 || amount > 127)
                    throw new ConfigurationFieldException("'Amount' field is not a valid amount");
                builder.amount(section.getInt("Amount"));
            }
            if (section.isInt("Damage")) {
                int damage = section.getInt("Damage");
                if (damage < 0)
                    throw new ConfigurationFieldException("'Damage' field is not a valid damage value");
                builder.damage(damage);
            }
            if (section.isInt("RepairCost")) {
                int repairCost = section.getInt("RepairCost");
                if (repairCost < 0)
                    throw new ConfigurationFieldException("'RepairCost' field is not a valid repair cost value");
                builder.repairCost(repairCost);
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
                builder.armorTrim(new ArmorTrim(material, pattern));
            }
            if (section.isString("ItemName")) {
                String itemName = section.getString("ItemName");
                builder.itemName(TextColor.PARSE(itemName));
            }
            if (section.isBoolean("HideToolTip")) {
                boolean hideToolTip = section.getBoolean("HideToolTip");
                builder.hideToolTip(hideToolTip);
            }
            if (section.isBoolean("EnchantmentGlintOverride")) {
                boolean enchantmentGlintOverride = section.getBoolean("EnchantmentGlintOverride");
                builder.enchantmentGlintOverride(enchantmentGlintOverride);
            }
            if (section.isBoolean("FireResistant")) {
                boolean fireResistant = section.getBoolean("FireResistant");
                builder.fireResistant(fireResistant);
            }
            if (section.isInt("MaxStackSize")) {
                int maxStackSize = section.getInt("MaxStackSize");
                builder.maxStackSize(maxStackSize);
            }
            if (section.isInt("MaxDamage")) {
                int maxDamage = section.getInt("MaxDamage");
                if (maxDamage < 0)
                    throw new ConfigurationFieldException("'MaxDamage' field is not a valid max damage value");
                builder.maxDamage(maxDamage);
            }
            if (section.isString("Rarity")) {
                ItemRarity rarity = ItemRarity.valueOf(section.getString("Rarity"));
                builder.rarity(rarity);
            }
            if (section.isString("TooltipStyle")) {
                String tooltipStyle = section.getString("TooltipStyle");
                String[] split = tooltipStyle.split(":");
                if (split.length != 2)
                    throw new ConfigurationFieldException("'TooltipStyle' field is not valid: " + tooltipStyle);
                Key key = Key.key(split[0], split[1]);
                builder.tooltipStyle(key);
            }
            if (section.isConfigurationSection("Equippable")) {
                ConfigurationSection equippableSection = section.getConfigurationSection("Equippable");
                String path = "Equipabble";
                @Nullable String equipmentSlot = equippableSection.getString("EquipmentSlot");
                if (equipmentSlot == null)
                    throw new ConfigurationFieldException("'" + path + ".EquipmentSlot' field is not valid");
                EquipmentSlot slot;
                try {
                    slot = EquipmentSlot.valueOf(equipmentSlot);
                } catch (IllegalArgumentException exception) {
                    throw new ConfigurationFieldException("'" + path + ".EquipmentSlot' field is not valid");
                }
                var equippable = Equippable.equippable(slot);
                @Nullable String cameraOverlay = equippableSection.getString("CameraOverlay");
                if (cameraOverlay != null) {
                    String[] split = cameraOverlay.split(":");
                    if (split.length != 2)
                        throw new ConfigurationFieldException("'" + path + ".CameraOverlay' field is not valid: " + cameraOverlay);
                    Key key = Key.key(split[0], split[1]);
                    equippable.cameraOverlay(key);
                }
                @Nullable String equipSound = equippableSection.getString("EquipSound");
                if (equipSound != null) {
                    String[] split = equipSound.split(":");
                    if (split.length != 2)
                        throw new ConfigurationFieldException("'" + path + ".EquipSound' field is not valid: " + equipSound);
                    Key key = Key.key(split[0], split[1]);
                    equippable.equipSound(key);
                }
                @Nullable String assetId = equippableSection.getString("AssetId");
                if (assetId != null) {
                    String[] split = assetId.split(":");
                    if (split.length != 2)
                        throw new ConfigurationFieldException("'" + path + ".AssetId' field is not valid: " + assetId);
                    Key key = Key.key(split[0], split[1]);
                    equippable.assetId(key);
                }
                @Nullable String allowedEntities = section.getString("AllowedEntities");
                if (allowedEntities != null) {
                    RegistryKey<EntityType> registryKey = RegistryKey.ENTITY_TYPE;
                    TagKey<EntityType> tagKey = registryKey.tagKey(allowedEntities);
                    Registry<EntityType> registry = RegistryAccess.registryAccess().getRegistry(registryKey);
                    if (!registry.hasTag(tagKey)) {
                        throw new ConfigurationFieldException("'" + path + ".AllowedEntities' is not valid: " + allowedEntities);
                    }
                    Tag<EntityType> tag = registry.getTag(tagKey);
                    equippable.allowedEntities(tag);
                }
                boolean damageOnHurt = equippableSection.getBoolean("DamageOnHurt", false);
                equippable.damageOnHurt(damageOnHurt);
                boolean dispensable = equippableSection.getBoolean("Dispensable", false);
                equippable.dispensable(dispensable);
                boolean swappable = equippableSection.getBoolean("Swappable", false);
                equippable.swappable(swappable);

                builder.equippable(equippable.build());
            }
            if (section.isBoolean("Glider")) {
                boolean glider = section.getBoolean("Glider");
                if (glider)
                    builder.glider();
            }
            if (section.isConfigurationSection("Consumable")) {
                ConfigurationSection consumableSection = section.getConfigurationSection("Consumable");
                var consumable = Consumable.consumable();
                if (consumableSection.isDouble("ConsumeSeconds")) {
                    double consumeSeconds = consumableSection.getDouble("ConsumeSeconds");
                    consumable.consumeSeconds((float) consumeSeconds);
                }
                if (consumableSection.isBoolean("HasConsumeParticles")) {
                    consumable.hasConsumeParticles(consumableSection.getBoolean("HasConsumeParticles"));
                }
                if (consumableSection.isString("Animation")) {
                    String animation = consumableSection.getString("Animation");
                    try {
                        ItemUseAnimation useAnimation = ItemUseAnimation.valueOf(animation);
                        consumable.animation(useAnimation);
                    } catch (IllegalArgumentException exception) {
                        throw new ConfigurationFieldException("Consumable.Animation is not a valid ItemUseAnimation");
                    }
                }
                builder.consumable(consumable.build());
            }
            if (section.isConfigurationSection("Tool")) {
                ConfigurationSection toolSection = section.getConfigurationSection("Tool");
                var tool = Tool.tool();
                if (toolSection.isInt("DamagePerBlock"))
                    tool.damagePerBlock(toolSection.getInt("DamagePerBlock"));
                if (toolSection.isDouble("DefaultMiningSpeed")) {
                    double miningSpeed = toolSection.getDouble("DefaultMiningSpeed");
                    tool.defaultMiningSpeed((float) miningSpeed);
                }
                ConfigurationSection rulesSection = toolSection.getConfigurationSection("Rules");
                if (rulesSection != null) {
                    rulesSection.getKeys(false).forEach(ruleSectionName -> {
                        ConfigurationSection ruleSection = rulesSection.getConfigurationSection(ruleSectionName);
                        if (ruleSection == null) {
                            return;
                        }
                        String path = "Tool." + ruleSectionName;
                        @Nullable String blocks = ruleSection.getString("Blocks");
                        if (blocks == null) {
                            return;
                        }
                        RegistryKey<BlockType> registryKey = RegistryKey.BLOCK;
                        TagKey<BlockType> tagKey = registryKey.tagKey(blocks);
                        Registry<BlockType> registry = RegistryAccess.registryAccess().getRegistry(registryKey);
                        if (!registry.hasTag(tagKey)) {
                            throw new ConfigurationFieldException("'" + path + ".Blocks' is not valid: " + blocks);
                        }
                        Tag<BlockType> tag = registry.getTag(tagKey);
                        @Nullable Double speed;
                        if (ruleSection.isDouble("Speed"))
                            speed = ruleSection.getDouble("Speed");
                        else
                            speed = null;
                        boolean correctForDrops = ruleSection.getBoolean("CorrectForDrops", false);
                        TriState triState = TriState.byBoolean(correctForDrops);
                        Tool.Rule rule = Tool.rule(tag, speed == null ? null : speed.floatValue(), triState);
                        tool.addRule(rule);
                    });
                }
                builder.tool(tool.build());
            }
            if (section.isConfigurationSection("Food")) {
                ConfigurationSection foodSection = section.getConfigurationSection("Food");
                var properties = FoodProperties.food();
                if (foodSection.isInt("Nutrition"))
                    properties.nutrition(foodSection.getInt("Nutrition"));
                if (foodSection.isDouble("Saturation")) {
                    float saturation = (float) foodSection.getDouble("Saturation");
                    properties.saturation(saturation);
                }
                if (foodSection.isBoolean("CanAlwaysEat"))
                    properties.canAlwaysEat(foodSection.getBoolean("CanAlwaysEat"));
                builder.food(properties.build());
            }
            if (section.isString("DisplayName")) {
                builder.displayName(TextColor.PARSE(section
                        .getString("DisplayName")));
            }
            if (section.isList("Lore")) {
                List<String> input = section.getStringList("Lore");
                List<String> lore = new ArrayList<>();
                input.forEach(string -> lore.add(TextColor.PARSE(string)));
                builder.lore(lore);
            }
            if (section.isBoolean("Unbreakable")) {
                builder.unbreakable(section.getBoolean("Unbreakable"));
            }
            if (section.isString("Color")) {
                builder.color(parseColor(section.getString("Color")));
            }
            if (section.isList("Enchantments")) {
                List<String> enchantNames = section.getStringList("Enchantments");
                builder.deserializeAndEnchant(enchantNames);
            }
            if (section.isConfigurationSection("Attributes")) {
                ConfigurationSection attributes = section.getConfigurationSection("Attributes");
                Uber<ItemStackBuilder> uber = Uber.drive(builder);
                attributes.getKeys(false).forEach(key -> {
                    String attributeKey = key.toLowerCase(Locale.ROOT);
                    if (!attributes.isConfigurationSection(key))
                        throw new ConfigurationFieldException("Attribute '" + key + "' is not valid");
                    ConfigurationSection attributeSection = attributes.getConfigurationSection(key);
                    try {
                        String name = attributes.getString("Name", UUID.randomUUID().toString());
                        Attribute attribute = registryAccess.getRegistry(RegistryKey.ATTRIBUTE).get(Key.key(attributeKey));
                        if (!attributeSection.isDouble("Amount"))
                            throw new ConfigurationFieldException("Attribute '" + key + "' has an invalid amount (DECIMAL NUMBER)");
                        double amount = attributeSection.getDouble("Amount");
                        if (!attributeSection.isString("Operation"))
                            throw new ConfigurationFieldException("Attribute '" + key + "' is missing 'Operation' field");
                        EquipmentSlotGroup equipmentSlot;
                        String readEquipmentSlotGroup = attributeSection.getString("EquipmentSlotGroup");
                        if (readEquipmentSlotGroup == null)
                            readEquipmentSlotGroup = attributeSection.getString("EquipmentSlot");
                        if (readEquipmentSlotGroup != null) {
                            try {
                                equipmentSlot = EquipmentSlotGroup.getByName(readEquipmentSlotGroup);
                            } catch (IllegalArgumentException exception) {
                                throw new ConfigurationFieldException("EquipmentSlot '" + readEquipmentSlotGroup + "' is not valid");
                            }
                        } else
                            equipmentSlot = null;
                        AttributeModifier.Operation operation = AttributeModifier.Operation.valueOf(attributeSection.getString("Operation"));
                        uber.talk(uber.thanks().attribute(attribute, name, amount, operation, equipmentSlot));
                    } catch (IllegalArgumentException exception) {
                        throw new ConfigurationFieldException("Attribute '" + key + "' has an invalid Operation");
                    }
                });
                uber.thanks();
            }
            builder.hideAll();
            boolean showAll = section.getBoolean("ShowAllItemFlags", false);
            if (showAll)
                builder.showAll();
            if (section.isList("ItemFlags")) {
                List<String> flagNames = section.getStringList("ItemFlags");
                builder.deserializeAndFlag(flagNames);
            }
        };
        return new OmniStack(stackSupplier, builderConsumer);
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
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Color " + color + " is not a valid color.");
        }
    }

    public static String parse(Color color) {
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }
}
