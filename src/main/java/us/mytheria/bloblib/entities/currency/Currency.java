package us.mytheria.bloblib.entities.currency;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.BlobObject;
import us.mytheria.bloblib.entities.logger.BlobPluginLogger;
import us.mytheria.bloblib.utilities.Formatter;
import us.mytheria.bloblib.utilities.TextColor;

import java.io.File;
import java.text.DecimalFormat;

public class Currency implements BlobObject {
    private final String display;
    private final String key;
    private final double initialBalance;
    private final boolean isPersistent;
    private final DecimalFormat decimalFormat;

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
                    String pattern, String key) {
        this.display = display;
        this.key = key;
        this.initialBalance = initialBalance;
        this.isPersistent = isPersistent;
        this.decimalFormat = new DecimalFormat(pattern);
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

    public static Currency fromFile(File file) {
        BlobPluginLogger logger = BlobLib.getAnjoLogger();
        String fileName = file.getName();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        String display = TextColor.PARSE(yamlConfiguration.getString("Display"));
        double initialBalance = yamlConfiguration.getDouble("InitialBalance");
        boolean isPersistent = yamlConfiguration.getBoolean("Persistent");
        String pattern = yamlConfiguration.getString("DecimalFormat");
        String key = FilenameUtils.removeExtension(fileName);
        return new Currency(display, initialBalance, isPersistent, pattern, key);
    }
}