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
import us.mytheria.bloblib.utilities.TextColor;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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

    public ItemStackModder displayName(String name, char colorChar) {
        return itemMeta(itemMeta -> itemMeta.setDisplayName(TextColor.CUSTOM_PARSE(colorChar, name)));
    }

    public ItemStackModder displayName(String name) {
        return displayName(name, '&');
    }

    public ItemStackModder lore(String line, char colorChar) {
        return lore(List.of(line), colorChar);
    }

    public ItemStackModder lore(String line) {
        return lore(line, '&');
    }

    public ItemStackModder lore(char colorChar, String... lore) {
        List<String> list = Stream.of(lore).map(line -> TextColor.CUSTOM_PARSE(colorChar, line)).toList();
        return itemMeta(itemMeta -> itemMeta.setLore(list));
    }

    public ItemStackModder lore(String... lore) {
        return lore('&', lore);
    }

    public ItemStackModder lore(List<String> lore, char colorChar) {
        List<String> list = lore.stream().map(line -> TextColor.CUSTOM_PARSE(colorChar, line)).toList();
        return itemMeta(itemMeta -> itemMeta.setLore(list));
    }

    public ItemStackModder lore(List<String> lore) {
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
     * Replaces in both displayName and lore if available
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

    /**
     * Modifies the displayName and lore if available
     *
     * @param function the function to modify the strings
     * @return the modified ItemStackModder
     */
    public ItemStackModder modify(Function<String, String> function) {
        itemMeta(itemMeta -> {
            displayName(function.apply(itemMeta.getDisplayName()));
            if (itemMeta.hasLore())
                lore(itemMeta.getLore().stream()
                        .map(function)
                        .toList());
        });
        return this;
    }

    private ItemStackModder regexReplace(String match, Function<String, String> function) {
        Pattern pattern = Pattern.compile(match);
        return modify(s -> {
            Matcher matcher = pattern.matcher(s);
            StringBuilder sb = new StringBuilder();
            while (matcher.find()) {
                matcher.appendReplacement(sb, function.apply(matcher.group(1)));
            }
            matcher.appendTail(sb);
            return sb.toString();
        });
    }

    /**
     * Will match all occurrences of the given regex and replace them with the
     * result of the function by using a wildcard.
     * <p>
     * Example:
     * matchReplace("%flag@%", "@", s -> s); //Will set all flag placeholders as
     * //whatever the flag is.
     * <p>
     *
     * @param match    The regex to match
     * @param wildcard The wildcard to use
     * @param function The function to use
     * @return The modified translatable
     */
    public ItemStackModder matchReplace(String match, String wildcard, Function<String, String> function) {
        String regex = match.replace(wildcard, "(.*?)");
        return regexReplace(regex, function);
    }

    /**
     * Will match all occurrences of the given regex and replace them with the
     * result of the function.
     * Will use '@' as the wildcard.
     *
     * @param match    The regex to match
     * @param function The function to use
     * @return The modified translatable
     */
    public ItemStackModder matchReplace(String match, Function<String, String> function) {
        return matchReplace(match, "@", function);
    }
}
