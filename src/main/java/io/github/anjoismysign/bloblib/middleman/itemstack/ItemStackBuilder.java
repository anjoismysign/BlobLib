package io.github.anjoismysign.bloblib.middleman.itemstack;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.utilities.TextColor;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.ItemAdventurePredicate;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;

public final class ItemStackBuilder {
    private final ItemStack itemStack;

    private static final ItemFlag[] ALL_CONSTANTS = ItemFlag.values();

    public static ItemStackBuilder build(ItemStack itemStack) {
        return new ItemStackBuilder(new ItemStack(itemStack));
    }

    public static ItemStackBuilder build(Material material) {
        return build(material, 1);
    }

    public static ItemStackBuilder build(Material material, int amount) {
        ItemStack itemStack = new ItemStack(material, amount);
        if (material == Material.POISONOUS_POTATO)
            itemStack.unsetData(DataComponentTypes.FOOD);
        return new ItemStackBuilder(itemStack);
    }

    public static ItemStackBuilder build(String material) {
        return build(material, 1);
    }

    public static ItemStackBuilder build(String material, int amount) {
        Material mat;
        try {
            mat = Material.valueOf(material);
        } catch ( IllegalArgumentException exception ) {
            mat = Material.DIRT;
            Bukkit.getLogger().info("Material " + material + " is not a valid material. Using DIRT instead.");
        }
        return build(mat, amount);
    }

    private ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = Objects.requireNonNull(itemStack, "itemStack cannot be null");
    }

    private ItemStackBuilder damageable(Consumer<Damageable> consumer) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof Damageable damageable) {
            consumer.accept(damageable);
            this.itemStack.setItemMeta(itemMeta);
        }
        return this;
    }

    private ItemStackBuilder skullMeta(Consumer<SkullMeta> consumer) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof SkullMeta skullMeta) {
            consumer.accept(skullMeta);
            this.itemStack.setItemMeta(itemMeta);
        }
        return this;
    }

    public ItemStackBuilder playerProfile(@NotNull PlayerProfile profile) {
        return skullMeta(skullMeta -> skullMeta.setPlayerProfile(profile));
    }

    public ItemStackBuilder resolvableProfile(@NotNull ResolvableProfile profile) {
        itemStack.setData(DataComponentTypes.PROFILE, profile);
        return this;
    }

    public ItemStackBuilder canPlaceOn(@NotNull ItemAdventurePredicate predicate) {
        itemStack.setData(DataComponentTypes.CAN_PLACE_ON, predicate);
        return this;
    }

    public ItemStackBuilder canBreak(@NotNull ItemAdventurePredicate predicate) {
        itemStack.setData(DataComponentTypes.CAN_BREAK, predicate);
        return this;
    }

    public ItemStackBuilder tooltipStyle(@NotNull Key key) {
        itemStack.setData(DataComponentTypes.TOOLTIP_STYLE, key);
        return this;
    }

    public ItemStackBuilder equippable(@NotNull Equippable equippable) {
        itemStack.setData(DataComponentTypes.EQUIPPABLE, equippable);
        return this;
    }

    public ItemStackBuilder attributeModifiers(@NotNull ItemAttributeModifiers modifiers){
        itemStack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, modifiers);
        return this;
    }

    public ItemStackBuilder glider() {
        itemStack.setData(DataComponentTypes.GLIDER);
        return this;
    }

    public ItemStackBuilder damageResistant(@NotNull DamageResistant damageResistant) {
        itemStack.setData(DataComponentTypes.DAMAGE_RESISTANT, damageResistant);
        return this;
    }

    public ItemStackBuilder consumable(@NotNull Consumable consumable) {
        itemStack.setData(DataComponentTypes.CONSUMABLE, consumable);
        return this;
    }

    public ItemStackBuilder itemModel(@NotNull NamespacedKey itemModel) {
        return itemMeta(itemMeta -> itemMeta.setItemModel(itemModel));
    }

    public ItemStackBuilder tool(Tool tool) {
        itemStack.setData(DataComponentTypes.TOOL, tool);
        return this;
    }

    public ItemStackBuilder damage(int damage) {
        return damageable(damageable -> damageable.setDamage(damage));
    }

    public ItemStackBuilder maxDamage(int maxDamage) {
        return damageable(damageable -> damageable.setMaxDamage(maxDamage));
    }

    private ItemStackBuilder repariable(Consumer<Repairable> consumer) {
        ItemMeta m = this.itemStack.getItemMeta();
        if (m instanceof Repairable repairable) {
            consumer.accept(repairable);
            this.itemStack.setItemMeta(m);
        }
        return this;
    }

    public ItemStackBuilder repairCost(int repairCost) {
        return repariable(repairable -> repairable.setRepairCost(repairCost));
    }

    private ItemStackBuilder armor(Consumer<ArmorMeta> consumer) {
        ItemMeta m = this.itemStack.getItemMeta();
        if (m instanceof ArmorMeta armorMeta) {
            consumer.accept(armorMeta);
            this.itemStack.setItemMeta(m);
        }
        return this;
    }

    public ItemStackBuilder armorTrim(ArmorTrim armorTrim) {
        return armor(armorMeta -> armorMeta.setTrim(armorTrim));
    }

    private ItemStackBuilder itemMeta(Consumer<ItemMeta> consumer) {
        ItemMeta m = this.itemStack.getItemMeta();
        if (m != null) {
            consumer.accept(m);
            this.itemStack.setItemMeta(m);
        }
        return this;
    }

    public ItemStackBuilder itemName(String name) {
        return itemMeta(itemMeta -> itemMeta.setItemName(TextColor.PARSE(name)));
    }

    public ItemStackBuilder hideToolTip(boolean hideToolTip) {
        return itemMeta(itemMeta -> itemMeta.setHideTooltip(hideToolTip));
    }

    public ItemStackBuilder enchantmentGlintOverride(boolean glint) {
        return itemMeta(itemMeta -> itemMeta.setEnchantmentGlintOverride(glint));
    }

    public ItemStackBuilder fireResistant(boolean fireResistant) {
        return itemMeta(itemMeta -> itemMeta.setFireResistant(fireResistant));
    }

    public ItemStackBuilder maxStackSize(int maxStackSize) {
        return itemMeta(itemMeta -> itemMeta.setMaxStackSize(maxStackSize));
    }

    public ItemStackBuilder rarity(ItemRarity rarity) {
        return itemMeta(itemMeta -> itemMeta.setRarity(rarity));
    }

    public ItemStackBuilder food(@NotNull FoodProperties foodProperties) {
        itemStack.setData(DataComponentTypes.FOOD, foodProperties);
        return this;
    }

    public ItemStackBuilder hideAll() {
        var display = TooltipDisplay.tooltipDisplay()
                .addHiddenComponents(DataComponentTypes.ATTRIBUTE_MODIFIERS)
                .addHiddenComponents(DataComponentTypes.ENCHANTMENTS)
                .addHiddenComponents(DataComponentTypes.CAN_BREAK)
                .addHiddenComponents(DataComponentTypes.CAN_PLACE_ON)
                .addHiddenComponents(DataComponentTypes.POTION_CONTENTS)
                .addHiddenComponents(DataComponentTypes.POTION_DURATION_SCALE)
                .addHiddenComponents(DataComponentTypes.DYED_COLOR)
                .addHiddenComponents(DataComponentTypes.TRIM)
                .addHiddenComponents(DataComponentTypes.UNBREAKABLE)
                .addHiddenComponents(DataComponentTypes.STORED_ENCHANTMENTS)
                .build();

        itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, display);
        return this;
    }

    public ItemStackBuilder showAll() {
        return unflag(ALL_CONSTANTS);
    }

    public ItemStackBuilder flag(ItemFlag... flags) {
        return itemMeta(itemMeta -> itemMeta.addItemFlags(flags));
    }

    public ItemStackBuilder flag(Collection<ItemFlag> flags) {
        return flag(flags.toArray(new ItemFlag[0]));
    }

    public ItemStackBuilder deserializeAndFlag(List<String> flags) {
        List<ItemFlag> itemFlags = new ArrayList<>();
        for (String flag : flags) {
            try {
                itemFlags.add(ItemFlag.valueOf(flag));
            } catch ( IllegalArgumentException exception ) {
                Bukkit.getLogger().info("ItemFlag " + flag + " is not a valid ItemFlag.");
            }
        }
        return flag(itemFlags);
    }

    public ItemStackBuilder unflag(ItemFlag... flags) {
        return itemMeta(itemMeta -> itemMeta.removeItemFlags(flags));
    }

    public ItemStackBuilder unflag(Collection<ItemFlag> flags) {
        return unflag(flags.toArray(new ItemFlag[0]));
    }

    public ItemStackBuilder displayName(String name, char colorChar) {
        return itemMeta(itemMeta -> itemMeta.setDisplayName(TextColor.CUSTOM_PARSE(colorChar, name)));
    }

    public ItemStackBuilder displayName(String name) {
        return displayName(name, '&');
    }

    public ItemStackBuilder lore(String line, char colorChar) {
        return lore(List.of(line), colorChar);
    }

    public ItemStackBuilder lore(String line) {
        return lore(line, '&');
    }

    public ItemStackBuilder lore(char colorChar, String... lore) {
        List<String> list = List.of(lore);
        List<String> dupe = new ArrayList<>();
        list.forEach(s -> dupe.add(TextColor.CUSTOM_PARSE(colorChar, s)));
        return itemMeta(itemMeta -> itemMeta.setLore(dupe));
    }

    public ItemStackBuilder lore(String... lore) {
        return lore('&', lore);
    }

    public ItemStackBuilder lore(List<String> lore, char colorChar) {
        List<String> list = lore.stream().map(s -> TextColor.CUSTOM_PARSE(colorChar, s)).toList();
        return itemMeta(itemMeta -> itemMeta.setLore(list));
    }

    public ItemStackBuilder lore(List<String> lore) {
        return lore(lore, '&');
    }

    private List<String> getLore() {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;
        return meta.getLore();
    }

    /**
     * Clears the lore of the item.
     */
    public ItemStackBuilder lore() {
        return itemMeta(itemMeta -> itemMeta.setLore(new ArrayList<>()));
    }

    public ItemStackBuilder customModelData(int amount) {
        return itemMeta(itemMeta -> itemMeta.setCustomModelData(amount));
    }

    private ItemStackBuilder itemStack(Consumer<ItemStack> consumer) {
        consumer.accept(this.itemStack);
        return this;
    }

    public ItemStackBuilder amount(int amount) {
        return itemStack(itemStack -> itemStack.setAmount(amount));
    }

    public ItemStackBuilder type(Material material) {
        return itemStack(itemStack -> itemStack.setType(material));
    }

    public ItemStackBuilder enchant(Enchantment enchantment, int level) {
        return itemStack(itemStack -> itemStack.addUnsafeEnchantment(enchantment, level));
    }

    public ItemStackBuilder unenchant(Enchantment enchantment) {
        return itemStack(itemStack -> itemStack.removeEnchantment(enchantment));
    }

    public ItemStackBuilder enchant(Enchantment... enchantments) {
        return itemStack(itemStack -> {
            for (Enchantment enchantment : enchantments) {
                itemStack.addUnsafeEnchantment(enchantment, 1);
            }
        });
    }

    public ItemStackBuilder enchant(Collection<Enchantment> enchantments) {
        return itemStack(itemStack -> {
            for (Enchantment enchantment : enchantments) {
                itemStack.addUnsafeEnchantment(enchantment, 1);
            }
        });
    }

    public ItemStackBuilder enchant(Map<Enchantment, Integer> enchantments) {
        return itemStack(itemStack -> {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                itemStack.addUnsafeEnchantment(entry.getKey(), entry.getValue());
            }
        });
    }

    public ItemStackBuilder deserializeAndEnchant(Collection<String> serializedEnchantments) {
        HashMap<Enchantment, Integer> enchantments = new HashMap<>();
        serializedEnchantments.forEach(element -> {
            String[] split = element.split(",");
            if (split.length != 2) {
                Bukkit.getLogger().severe("Invalid element inside 'Enchantments': " + element);
                return;
            }
            String key = split[0];
            int level;
            try {
                level = Integer.parseInt(split[1]);
            } catch ( NumberFormatException exception ) {
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
        return enchant(enchantments);
    }

    public ItemStackBuilder unenchant(Enchantment... enchantments) {
        return itemStack(itemStack -> {
            for (Enchantment enchantment : enchantments) {
                itemStack.removeEnchantment(enchantment);
            }
        });
    }

    public ItemStackBuilder enchant(Enchantment enchantment) {
        return enchant(enchantment, 1);
    }

    public ItemStackBuilder clearEnchantments() {
        return itemMeta(itemStack -> itemStack.getEnchants().keySet().forEach(itemStack::removeEnchant));
    }

    public ItemStackBuilder color(Color color) {
        return itemStack(itemStack -> {
            Material type = itemStack.getType();
            if (type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_HELMET || type == Material.LEATHER_LEGGINGS) {
                LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
                meta.setColor(color);
                itemStack.setItemMeta(meta);
            }
        });
    }

    public ItemStackBuilder attribute(
            Attribute attribute,
            @NotNull String name,
            double amount,
            @NotNull AttributeModifier.Operation operation,
            @Nullable EquipmentSlotGroup equipmentSlotGroup) {
        return itemMeta(itemMeta -> {
            try {
                NamespacedKey key = new NamespacedKey(BlobLib.getInstance(), name);
                AttributeModifier modifier = new AttributeModifier(key, amount, operation, equipmentSlotGroup);
                itemMeta.addAttributeModifier(attribute, modifier);
            } catch ( Exception exception ) {
                Bukkit.getLogger().log(Level.SEVERE, exception, () -> "Failed to add attribute modifier");
            }
        });
    }

    public ItemStackBuilder unbreakable(boolean unbreakable) {
        return itemMeta(itemMeta -> itemMeta.setUnbreakable(unbreakable));
    }

    public ItemStack build() {
        return this.itemStack;
    }


}
