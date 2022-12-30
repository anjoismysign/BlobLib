package us.mytheria.bloblib.itemstack;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import us.mytheria.bloblib.entities.Rep;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ItemStackModder {
    private final ItemStack itemStack;

    private static final ItemFlag[] ALL_CONSTANTS = ItemFlag.values();

    public static ItemStackModder mod(ItemStack itemStack) {
        return new ItemStackModder(itemStack).hideAll();
    }

    private ItemStackModder(ItemStack itemStack) {
        this.itemStack = Objects.requireNonNull(itemStack, "itemStack cannot be null");
    }

    private ItemStackModder itemMeta(Consumer<ItemMeta> consumer) {
        ItemMeta m = this.itemStack.getItemMeta();
        if (m != null) {
            consumer.accept(m);
            this.itemStack.setItemMeta(m);
        }
        return this;
    }

    public ItemStackModder hideAll() {
        return flag(ALL_CONSTANTS);
    }

    public ItemStackModder showAll() {
        return unflag(ALL_CONSTANTS);
    }

    public ItemStackModder flag(ItemFlag... flags) {
        return itemMeta(itemMeta -> itemMeta.addItemFlags(flags));
    }

    public ItemStackModder flag(Collection<ItemFlag> flags) {
        return flag(flags.toArray(new ItemFlag[0]));
    }

    public ItemStackModder deserializeAndFlag(List<String> flags) {
        List<ItemFlag> itemFlags = new ArrayList<>();
        for (String flag : flags) {
            try {
                itemFlags.add(ItemFlag.valueOf(flag));
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().info("ItemFlag " + flag + " is not a valid ItemFlag.");
            }
        }
        return flag(itemFlags);
    }

    public ItemStackModder unflag(ItemFlag... flags) {
        return itemMeta(itemMeta -> itemMeta.removeItemFlags(flags));
    }

    public ItemStackModder unflag(Collection<ItemFlag> flags) {
        return unflag(flags.toArray(new ItemFlag[0]));
    }

    public ItemStackModder displayName(String name) {
        return itemMeta(itemMeta -> itemMeta.setDisplayName(name));
    }

    public ItemStackModder lore(String line) {
        return lore(List.of(line));
    }

    public ItemStackModder lore(String... lore) {
        return itemMeta(itemMeta -> itemMeta.setLore(List.of(lore)));
    }

    public ItemStackModder lore(List<String> lore) {
        return itemMeta(itemMeta -> itemMeta.setLore(lore));
    }

    public ItemStackModder lore(Function<List<String>, List<String>> lore) {
        return itemMeta(itemMeta -> itemMeta.setLore(lore.apply(itemMeta.getLore())));
    }

    public ItemStackModder lore(List<String> lore, Rep... reps) {
        return lore(Rep.lace(lore, reps));
    }

    private List<String> getLore() {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;
        return meta.getLore();
    }

    public ItemStackModder lore(Rep... reps) {
        List<String> lore = getLore();
        if (lore == null) return this;
        return lore(Rep.lace(lore, reps));
    }

    /**
     * Clears the lore of the item.
     */
    public ItemStackModder lore() {
        return itemMeta(itemMeta -> itemMeta.setLore(new ArrayList<>()));
    }

    public ItemStackModder customModelData(int amount) {
        return itemMeta(itemMeta -> itemMeta.setCustomModelData(amount));
    }

    private ItemStackModder itemStack(Consumer<ItemStack> consumer) {
        consumer.accept(this.itemStack);
        return this;
    }

    public ItemStackModder amount(int amount) {
        return itemStack(itemStack -> itemStack.setAmount(amount));
    }

    public ItemStackModder type(Material material) {
        return itemStack(itemStack -> itemStack.setType(material));
    }

    public ItemStackModder enchant(Enchantment enchantment, int level) {
        return itemStack(itemStack -> itemStack.addUnsafeEnchantment(enchantment, level));
    }

    public ItemStackModder unenchant(Enchantment enchantment) {
        return itemStack(itemStack -> itemStack.removeEnchantment(enchantment));
    }

    public ItemStackModder enchant(Enchantment... enchantments) {
        return itemStack(itemStack -> {
            for (Enchantment enchantment : enchantments) {
                itemStack.addUnsafeEnchantment(enchantment, 1);
            }
        });
    }

    public ItemStackModder enchant(Collection<Enchantment> enchantments) {
        return itemStack(itemStack -> {
            for (Enchantment enchantment : enchantments) {
                itemStack.addUnsafeEnchantment(enchantment, 1);
            }
        });
    }

    public ItemStackModder enchant(Map<Enchantment, Integer> enchantments) {
        return itemStack(itemStack -> {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                itemStack.addUnsafeEnchantment(entry.getKey(), entry.getValue());
            }
        });
    }

    public ItemStackModder deserializeAndEnchant(Collection<String> serializedEnchantments) {
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
        return enchant(enchantments);
    }

    public ItemStackModder unenchant(Enchantment... enchantments) {
        return itemStack(itemStack -> {
            for (Enchantment enchantment : enchantments) {
                itemStack.removeEnchantment(enchantment);
            }
        });
    }

    public ItemStackModder enchant(Enchantment enchantment) {
        return enchant(enchantment, 1);
    }

    public ItemStackModder clearEnchantments() {
        return itemMeta(itemStack -> itemStack.getEnchants().keySet().forEach(itemStack::removeEnchant));
    }

    public ItemStackModder color(Color color) {
        return itemStack(itemStack -> {
            Material type = itemStack.getType();
            if (type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_HELMET || type == Material.LEATHER_LEGGINGS) {
                LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
                meta.setColor(color);
                itemStack.setItemMeta(meta);
            }
        });
    }

    public ItemStackModder unbreakable(boolean unbreakable) {
        return itemMeta(itemMeta -> itemMeta.setUnbreakable(unbreakable));
    }

    /**
     * Replaces in both displayName && lore if available
     *
     * @param regex       the regex to replace
     * @param replacement the replacement
     * @return
     */
    public ItemStackModder replace(String regex, String replacement) {
        return itemMeta(itemMeta -> {
            if (itemMeta.hasDisplayName()) {
                String name = itemMeta.getDisplayName();
                itemMeta.setDisplayName(name.replace(regex, replacement));
            }
            if (itemMeta.hasLore()) {
                List<String> current = itemMeta.getLore();
                List<String> toBeSet = new ArrayList<>();
                current.forEach(string -> {
                    string = string.replace(regex, replacement);
                    toBeSet.add(string);
                });
                itemMeta.setLore(toBeSet);
            }
        });
    }

    public ItemStackModder replace(Rep... reps) {
        for (Rep rep : reps) {
            replace(rep.getCheck(), rep.getReplace());
        }
        return this;
    }
}
