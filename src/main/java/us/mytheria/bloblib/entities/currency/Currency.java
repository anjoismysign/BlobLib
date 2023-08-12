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
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.BlobObject;
import us.mytheria.bloblib.itemstack.ItemStackReader;
import us.mytheria.bloblib.managers.BlobPlugin;
import us.mytheria.bloblib.managers.ManagerDirector;
import us.mytheria.bloblib.utilities.Formatter;
import us.mytheria.bloblib.utilities.TextColor;

import java.io.File;
import java.text.DecimalFormat;
import java.util.UUID;

public class Currency implements BlobObject {
    private final String display;
    private final String key;
    private final double initialBalance;
    private final boolean isPersistent;
    private final DecimalFormat decimalFormat;
    private final boolean isTangible;
    private final @Nullable ItemStack tangibleShape;
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
                    @Nullable ItemStack tangibleShape,
                    BlobPlugin plugin) {
        this.plugin = plugin;
        this.display = display;
        this.key = key;
        this.initialBalance = initialBalance;
        this.isPersistent = isPersistent;
        this.decimalFormat = new DecimalFormat(pattern);
        this.isTangible = isTangible;
        this.tangibleShape = tangibleShape;
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
     * If the currency is tangible, it will return the ItemStack
     * which is the way the currency is displayed in the game.
     *
     * @return the ItemStack, null if not tangible
     */
    @Nullable
    public ItemStack getTangibleShape() {
        return getTangibleShape(1, false);
    }

    /**
     * If the currency is tangible, it will return the ItemStack
     * which is the way the currency is displayed in the game.
     *
     * @param amount the amount to give
     * @return the ItemStack, null if not tangible
     */
    public ItemStack getTangibleShape(int amount) {
        return getTangibleShape(amount, false);
    }

    /**
     * If the currency is tangible, it will return the ItemStack
     * which is the way the currency is displayed in the game.
     *
     * @param amount the amount to give
     * @param unique whether the ItemStack should be unique
     * @return the ItemStack, null if not tangible
     */
    @Nullable
    public ItemStack getTangibleShape(int amount, boolean unique) {
        if (tangibleShape == null)
            return null;
        ItemStack itemStack = new ItemStack(tangibleShape);
        itemStack.setAmount(amount);
        if (unique) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null)
                return null;
            PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
            pdc.set(new NamespacedKey(BlobLib.getInstance(), "unique"),
                    PersistentDataType.STRING, UUID.randomUUID().toString());
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
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
        ItemStack tangibleShape = null;
        if (isTangible) {
            ConfigurationSection x = yamlConfiguration.getConfigurationSection("Tangible-Shape");
            if (x == null)
                throw new NullArgumentException("Tangible-Shape section is missing");
            tangibleShape = new ItemStack(ItemStackReader.read(x).build());
            ItemMeta itemMeta = tangibleShape.getItemMeta();
            PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
            pdc.set(director.getNamespacedKey("tangibleCurrency"), PersistentDataType.STRING, key);
            tangibleShape.setItemMeta(itemMeta);
        }
        return new Currency(display, initialBalance, isPersistent, pattern, key,
                isTangible, tangibleShape, director.getPlugin());
    }
}