package us.mytheria.bloblib.itemstack;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import us.mytheria.bloblib.entities.Rep;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ItemStackBuilder {
    private ItemStack itemStack;

    private static final ItemFlag[] ALL_CONSTANTS = ItemFlag.values();

    public static ItemStackBuilder build(ItemStack itemStack) {
        return new ItemStackBuilder(itemStack).hideAll();
    }

    public static ItemStackBuilder build(Material material) {
        return build(material, 1);
    }

    public static ItemStackBuilder build(Material material, int amount) {
        return build(new ItemStack(material, amount));
    }

    public static ItemStackBuilder build(String material) {
        return build(material, 1);
    }

    public static ItemStackBuilder build(String material, int amount) {
        Material mat;
        try {
            mat = Material.valueOf(material);
        } catch (IllegalArgumentException e) {
            mat = Material.DIRT;
            Bukkit.getLogger().info("Material " + material + " is not a valid material. Using DIRT instead.");
        }
        return build(mat, amount);
    }

    private ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = Objects.requireNonNull(itemStack, "itemStack cannot be null");
    }

    private ItemStackBuilder itemMeta(Consumer<ItemMeta> consumer) {
        ItemMeta m = this.itemStack.getItemMeta();
        if (m != null) {
            consumer.accept(m);
            this.itemStack.setItemMeta(m);
        }
        return this;
    }

    public ItemStackBuilder hideAll() {
        return addFlag(ALL_CONSTANTS);
    }

    public ItemStackBuilder showAll() {
        return removeFlag(ALL_CONSTANTS);
    }

    public ItemStackBuilder addFlag(ItemFlag... flags) {
        return itemMeta(itemMeta -> itemMeta.addItemFlags(flags));
    }

    public ItemStackBuilder removeFlag(ItemFlag... flags) {
        return itemMeta(itemMeta -> itemMeta.removeItemFlags(flags));
    }

    public ItemStackBuilder displayName(String name) {
        return itemMeta(itemMeta -> itemMeta.setDisplayName(name));
    }

    public ItemStackBuilder lore(String line) {
        return lore(List.of(line));
    }

    public ItemStackBuilder lore(String... lore) {
        return itemMeta(itemMeta -> itemMeta.setLore(List.of(lore)));
    }

    public ItemStackBuilder lore(List<String> lore) {
        return itemMeta(itemMeta -> itemMeta.setLore(lore));
    }

    public ItemStackBuilder lore(Function<List<String>, List<String>> lore) {
        return itemMeta(itemMeta -> itemMeta.setLore(lore.apply(itemMeta.getLore())));
    }

    public ItemStackBuilder lore(List<String> lore, Rep... reps) {
        return lore(Rep.lace(lore, reps));
    }

    private List<String> getLore() {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;
        return meta.getLore();
    }

    public ItemStackBuilder lore(Rep... reps) {
        List<String> lore = getLore();
        if (lore == null) return this;
        return lore(Rep.lace(lore, reps));
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

    public ItemStackBuilder unbreakable(boolean unbreakable) {
        return itemMeta(itemMeta -> itemMeta.setUnbreakable(unbreakable));
    }

    public ItemStack build() {
        return this.itemStack;
    }
}