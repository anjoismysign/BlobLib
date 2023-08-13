package us.mytheria.bloblib.entities.currency;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.BlobObject;
import us.mytheria.bloblib.exception.ConfigurationFieldException;
import us.mytheria.bloblib.itemstack.ItemStackReader;
import us.mytheria.bloblib.managers.BlobPlugin;
import us.mytheria.bloblib.managers.ManagerDirector;
import us.mytheria.bloblib.utilities.Formatter;
import us.mytheria.bloblib.utilities.TextColor;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class Currency implements BlobObject {
    private final String display;
    private final String key;
    private final double initialBalance;
    private final boolean isPersistent;
    private final DecimalFormat decimalFormat;
    private final boolean isTangible;
    private final @Nullable Map<String, ItemStack> tangibleShapes;
    private final BlobPlugin plugin;

    /**
     * Creates a new Currency.
     *
     * @param display        the display name
     * @param initialBalance the initial balance
     * @param isPersistent   whether the currency is persistent after rebirthing
     * @param pattern        the DecimalFormat pattern
     * @param key            the key
     */
    public Currency(String display, double initialBalance, boolean isPersistent,
                    String pattern, String key, boolean isTangible,
                    @Nullable Map<String, ItemStack> tangibleShapes,
                    BlobPlugin plugin) {
        this.plugin = plugin;
        this.display = display;
        this.key = key;
        this.initialBalance = initialBalance;
        this.isPersistent = isPersistent;
        this.decimalFormat = new DecimalFormat(pattern);
        this.isTangible = isTangible;
        if (tangibleShapes != null && tangibleShapes.isEmpty())
            this.tangibleShapes = null;
        else
            this.tangibleShapes = tangibleShapes;
    }

    /**
     * Returns the key of this currency.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * If the currency is tangible, which means that it
     * can be dropped on the ground, be picked up, etc.
     *
     * @return whether the currency is tangible
     */
    public boolean isTangible() {
        return isTangible;
    }

    /**
     * If the currency is tangible, it will return the ItemStacks
     * which is the way the currency is displayed in the game.
     * ItemStacks won't be unique, which means players can stack
     * them in inventories.
     *
     * @param amount the amount of this currency (not the item)
     *               to split into ItemStacks
     * @return the ItemStack, null if not tangible
     */
    public List<ItemStack> getTangibleShape(double amount) {
        return getTangibleShape(amount, false);
    }

    /**
     * If the currency is tangible, it will return the ItemStacks
     * which is the way the currency is displayed in the game.
     *
     * @param amount the amount of this currency (not the item)
     *               to split into ItemStacks
     * @param unique whether the ItemStacks should be unique
     * @return the ItemStacks. Null if not tangible, if amount is less
     * than or equal to zero or if it cannot proceed with current denominations
     */
    @Nullable
    public List<ItemStack> getTangibleShape(double amount, boolean unique) {
        if (!isTangible)
            return null;
        if (amount <= 0)
            return null;
        if (tangibleShapes == null)
            return null;
        List<ItemStack> list = new ArrayList<>();
        Map<BigDecimal, Integer> split = split(BigDecimal.valueOf(amount), tangibleShapes.keySet().toArray(new BigDecimal[0]));
        if (split.isEmpty())
            return null;
        split.forEach((x, y) -> {
            ItemStack clone = new ItemStack(tangibleShapes.get(x.toString()));
            clone.setAmount(y);
            if (unique) {
                ItemMeta itemMeta = clone.getItemMeta();
                PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
                pdc.set(new NamespacedKey(plugin, "unique"),
                        PersistentDataType.STRING, UUID.randomUUID().toString());
                clone.setItemMeta(itemMeta);
            }
            list.add(clone);
        });
        return list;
    }

    private static Map<BigDecimal, Integer> split(BigDecimal amount, BigDecimal[] denominations) {
        Map<BigDecimal, Integer> result = new HashMap<>();

        for (BigDecimal denomination : denominations) {
            int count = amount.divide(denomination, RoundingMode.FLOOR).intValue();
            if (count > 0) {
                result.put(denomination, count);
                amount = amount.subtract(denomination.multiply(BigDecimal.valueOf(count)));
            }
        }

        return result;
    }

    /**
     * How the currency is displayed in the game.
     *
     * @return the custom name
     */
    private String getDisplay() {
        return display;
    }

    /**
     * Returns a String that replaces inside customName '%amount%'
     * with the given amount.
     *
     * @param amount the amount to display
     * @return the formatted String
     */
    public String display(double amount) {
        return display.replace("%balance%", decimalFormat.format(amount)).replace(
                "%wattsBalance%", Formatter.WATTS((float) amount)).replace(
                "%bytesBalance%", Formatter.BYTES((float) amount)).replace(
                "%gramsBalance%", Formatter.GRAMS((float) amount));
    }

    /**
     * Returns the initial balance of this currency
     * for each new Lumber.
     *
     * @return the initial balance
     */
    public double getInitialBalance() {
        return initialBalance;
    }

    /**
     * Returns whether this currency is persistent after rebirthing.
     *
     * @return true if persistent, false if not
     */
    public boolean isPersistent() {
        return isPersistent;
    }

    /**
     * Returns the DecimalFormat used to format the currency.
     *
     * @return the DecimalFormat
     */
    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    @Override
    public File saveToFile(File directory) {
        File file = new File(directory + "/" + getKey() + ".yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        yamlConfiguration.set("Display", display);
        yamlConfiguration.set("InitialBalance", initialBalance);
        yamlConfiguration.set("Persistent", isPersistent);
        yamlConfiguration.set("DecimalFormat", decimalFormat.toPattern());
        yamlConfiguration.set("Is-Tangible", isTangible);
        try {
            yamlConfiguration.save(file);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return file;
    }

    public static Currency fromFile(File file, ManagerDirector director) {
        String fileName = file.getName();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        String display = TextColor.PARSE(yamlConfiguration.getString("Display"));
        double initialBalance = yamlConfiguration.getDouble("InitialBalance");
        boolean isPersistent = yamlConfiguration.getBoolean("Persistent");
        String pattern = yamlConfiguration.getString("DecimalFormat");
        String key = FilenameUtils.removeExtension(fileName);
        boolean isTangible = yamlConfiguration.getBoolean("Is-Tangible", false);
        final Map<String, ItemStack> tangibleShape = new HashMap<>();
        if (isTangible) {
            ConfigurationSection shapesSection = yamlConfiguration.getConfigurationSection("Tangible-Shapes");
            if (shapesSection == null)
                throw new NullArgumentException("Tangible-Shape section is missing");
            shapesSection.getKeys(false).forEach(reference -> {
                String a = "Tangible-Shapes." + reference;
                if (!shapesSection.isConfigurationSection(reference))
                    throw new ConfigurationFieldException("'" + a + "' is not valid");
                ConfigurationSection shape = shapesSection.getConfigurationSection(reference);
                if (!shape.isConfigurationSection("ItemStack"))
                    throw new ConfigurationFieldException("'" + a + ".ItemStack' is missing or not valid");
                ConfigurationSection itemSection = shape.getConfigurationSection("ItemStack");
                if (!shape.isDouble("Denomination"))
                    throw new ConfigurationFieldException("'" + a + ".Denomination' is missing or not valid");
                BigDecimal denomination = BigDecimal.valueOf(shape.getDouble("Denomination"));
                ItemStack builder = ItemStackReader.READ_OR_FAIL_FAST(itemSection).build();
                ItemMeta itemMeta = builder.getItemMeta();
                PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
                pdc.set(director.getNamespacedKey("tangibleCurrencyKey"), PersistentDataType.STRING, key);
                pdc.set(director.getNamespacedKey("tangibleCurrencyDenomination"), PersistentDataType.STRING, denomination.toString());
                builder.setItemMeta(itemMeta);
                tangibleShape.put(denomination.toString(), builder);
            });
        }
        return new Currency(display, initialBalance, isPersistent, pattern, key,
                isTangible, tangibleShape, director.getPlugin());
    }
}