package us.mytheria.bloblib.itemstack;

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
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.utilities.TextColor;

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
        return new ItemStackBuilder(new ItemStack(material, amount));
    }

    public static ItemStackBuilder build(String material) {
        return build(material, 1);
    }

    public static ItemStackBuilder build(String material, int amount) {
        Material mat;
        try {
            mat = Material.valueOf(material);
        } catch ( IllegalArgumentException e ) {
            mat = Material.DIRT;
            Bukkit.getLogger().info("Material " + material + " is not a valid material. Using DIRT instead.");
        }
        return build(mat, amount);
    }

    private ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = Objects.requireNonNull(itemStack, "itemStack cannot be null");
    }

    private ItemStackBuilder damageable(Consumer<Damageable> consumer) {
        ItemMeta m = this.itemStack.getItemMeta();
        if (m instanceof Damageable damageable) {
            consumer.accept(damageable);
            this.itemStack.setItemMeta(m);
        }
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
        return itemMeta(itemMeta -> itemMeta.setItemName(name));
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

    public ItemStackBuilder food(Consumer<FoodComponent> consumer) {
        return itemMeta(itemMeta -> {
            FoodComponent foodComponent = itemMeta.getFood();
            consumer.accept(foodComponent);
            itemMeta.setFood(foodComponent);
        });
    }

    public ItemStackBuilder hideAll() {
        return flag(ALL_CONSTANTS);
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
            } catch ( IllegalArgumentException e ) {
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
            } catch ( NumberFormatException e ) {
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
