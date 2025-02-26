package us.mytheria.bloblib.entities.currency;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Currency implements BlobObject {
    private final String display, key;
    private final Map<String, String> displayNames;
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
                    BlobPlugin plugin, Map<String, String> displayNames) {
        this.plugin = plugin;
        this.display = display;
        this.key = key;
        this.displayNames = displayNames;
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
     * @param amount the reminder of this currency (not the item)
     *               to split into ItemStacks
     * @return the TangibleShapeOperation. Check if it's valid
     * and if it has a reminder. Reminder means that the currency
     * cannot be split into ItemStacks with the current denominations
     * completely!
     */
    public TangibleShapeOperation getTangibleShape(double amount) {
        return getTangibleShape(amount, false);
    }

    /**
     * If the currency is tangible, it will return the ItemStacks
     * which is the way the currency is displayed in the game.
     *
     * @param amount the reminder of this currency (not the item)
     *               to split into ItemStacks
     * @param unique whether the ItemStacks should be unique
     * @return the TangibleShapeOperation. Check if it's valid
     * and if it has a reminder. Reminder means that the currency
     * cannot be split into ItemStacks with the current denominations
     * completely!
     */
    @NotNull
    public TangibleShapeOperation getTangibleShape(double amount, boolean unique) {
        if (!isTangible)
            return TangibleShapeOperation.INVALID();
        if (amount <= 0)
            return TangibleShapeOperation.INVALID();
        if (tangibleShapes == null)
            return TangibleShapeOperation.INVALID();
        List<ItemStack> list = new ArrayList<>();
        SplitResult result = split(BigDecimal.valueOf(amount), tangibleShapes.keySet().stream()
                .map(BigDecimal::new).toArray(BigDecimal[]::new));
        Map<BigDecimal, Integer> split = result.split();
        if (split.isEmpty())
            return TangibleShapeOperation.INVALID();
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
        return new TangibleShapeOperation(list, result.reminder());
    }

    private static SplitResult split(BigDecimal amount, BigDecimal[] denominations) {
        Map<BigDecimal, Integer> result = new HashMap<>();
        Arrays.sort(denominations, Comparator.reverseOrder());

        for (BigDecimal denomination : denominations) {
            int count = amount.divide(denomination, RoundingMode.FLOOR).intValue();
            if (count > 0) {
                result.put(denomination, count);
                amount = amount.subtract(denomination.multiply(BigDecimal.valueOf(count)));
            }
        }

        return new SplitResult(result, amount);
    }

    private record SplitResult(Map<BigDecimal, Integer> split, BigDecimal reminder) {
    }

    public record TangibleShapeOperation(List<ItemStack> shape, BigDecimal reminder) {
        private static TangibleShapeOperation INVALID() {
            return new TangibleShapeOperation(Collections.emptyList(), BigDecimal.ZERO);
        }

        public boolean hasReminder() {
            return reminder.compareTo(BigDecimal.ZERO) > 0;
        }

        public boolean isValid() {
            return !shape.isEmpty();
        }
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
     * Returns how its name is displayed in the game.
     * Will use the locale to get the name.
     *
     * @param locale the to get the locale from
     * @return the display name
     */
    @NotNull
    public String getDisplayName(@NotNull String locale) {
        String result = displayNames.get(locale);
        if (result == null)
            result = displayNames.get("en_us");
        Objects.requireNonNull(result, "Default Display-Name for locale '" + locale + "' is missing");
        return result;
    }

    /**
     * Returns how its name is displayed in the game.
     * Will use the player's locale to get the name.
     *
     * @param player the to get the locale from
     * @return the display name
     */
    @NotNull
    public String getDisplayName(@NotNull Player player) {
        String locale = player.getLocale();
        locale = BlobLibTranslatableAPI.getInstance().getRealLocale(locale);
        return getDisplayName(locale);
    }

    /**
     * Returns how its name is displayed in the game.
     * Will use the default locale to get the name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayNames.get("en_us");
    }

    /**
     * Returns a String that replaces inside customName '%reminder%'
     * with the given reminder.
     *
     * @param amount the reminder to display
     * @return the formatted String
     */
    public String display(double amount) {
        String balance = display.replace("%balance%", decimalFormat.format(amount));
        return Formatter.getInstance().formatAll(balance, amount);
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
        } catch ( Exception exception ) {
            exception.printStackTrace();
        }
        return file;
    }

    public static Currency fromFile(File file, ManagerDirector director) {
        String fileName = file.getName();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        String display = TextColor.PARSE(yamlConfiguration.getString("Display"));
        double initialBalance = yamlConfiguration.getDouble("Initial-Balance");
        boolean isPersistent = yamlConfiguration.getBoolean("Persistent");
        String pattern = yamlConfiguration.getString("Decimal-Format");
        String key = FilenameUtils.removeExtension(fileName);
        Map<String, String> displayNames = new HashMap<>();
        if (!yamlConfiguration.isConfigurationSection("Display-Name"))
            displayNames.put("en_us", key);
        else {
            ConfigurationSection namesSection = yamlConfiguration.getConfigurationSection("Display-Name");
            namesSection.getKeys(false).forEach(locale -> {
                if (!namesSection.isString(locale))
                    return;
                String value = namesSection.getString(locale);
                displayNames.put(locale, value);
            });
            if (!displayNames.containsKey("en_us"))
                displayNames.put("en_us", key);
        }
        boolean isTangible = yamlConfiguration.getBoolean("Is-Tangible", false);
        final Map<String, ItemStack> tangibleShape = new HashMap<>();
        if (isTangible) {
            ConfigurationSection shapesSection = yamlConfiguration.getConfigurationSection("Tangible-Shapes");
            if (shapesSection == null)
                throw new ConfigurationFieldException("Tangible-Shape section is missing");
            shapesSection.getKeys(false).forEach(reference -> {
                String a = "Tangible-Shapes." + reference;
                if (!shapesSection.isConfigurationSection(reference))
                    throw new ConfigurationFieldException("'" + a + "' is not valid");
                ConfigurationSection shape = shapesSection.getConfigurationSection(reference);
                if (!shape.isConfigurationSection("ItemStack"))
                    throw new ConfigurationFieldException("'" + a + ".ItemStack' is missing or not valid");
                ConfigurationSection itemSection = shape.getConfigurationSection("ItemStack");
                if (!shape.isDouble("Denomination"))
                    throw new ConfigurationFieldException("'" + a + ".Denomination' is missing or not valid (DECIMAL NUMBER)");
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
                isTangible, tangibleShape, director.getPlugin(), displayNames);
    }
}