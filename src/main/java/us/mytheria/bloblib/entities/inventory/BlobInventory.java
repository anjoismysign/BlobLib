package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author anjoismysign
 * <p>
 * A BlobInventory is an instance of a InventoryBuilder.
 * It's my own version/way for managing GUI's / inventories
 * using the Bukkit API.
 */
public class BlobInventory extends InventoryBuilder implements Cloneable {
    private Inventory inventory;
    private HashMap<String, ItemStack> defaultButtons;

    /**
     * A another way to create a BlobInventory from a file.
     * This way is a bit more efficient in some IDE's since after
     * typing BlobInventory and a dot, it will show you the
     * static methods, such as 'fromFile' and will also prompt
     * the file parameter you should already have.
     * <pre>
     *     BlobInventory inventory = new BlobInventory(file);
     *
     *     assert inventory.equals(BlobInventory.fromFile(file));
     *     </pre>
     *
     * @param file The file to load the inventory from.
     * @return The BlobInventory loaded from the file.
     * @deprecated Smart methods were made during development and are already
     * safe to use. Use {@link #fromFile(File)} instead which is
     * identical to this method.
     */
    @Deprecated
    public static BlobInventory smartFromFile(File file) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(
                Objects.requireNonNull(file, "'file' cannot be null!"));
        String title = ChatColor.translateAlternateColorCodes('&',
                configuration.getString("Title", configuration.getName() + ">NOT-SET"));
        int size = configuration.getInt("Size", -1);
        if (size < 0 || size % 9 != 0) {
            if (size < 0) {
                size = 54;
                Bukkit.getLogger().info(configuration.getName() + "'s Size is smaller than 0.");
                Bukkit.getLogger().info("This was probably due because you never set a Size.");
                Bukkit.getLogger().info("This is not possible in an inventory so it was set");
                Bukkit.getLogger().info("to '54' which is default.");
            } else {
                size = 54;
                Bukkit.getLogger().info(configuration.getName() + "'s Size is not a factor of 9.");
                Bukkit.getLogger().info("This is not possible in an inventory so it was set");
                Bukkit.getLogger().info("to '54' which is default.");
            }
        }
        BlobButtonManager buttonManager = BlobButtonManager.smartFromConfigurationSection(configuration.getConfigurationSection("Buttons"));
        return new BlobInventory(title, size, buttonManager);
    }

    /**
     * A another way to create a BlobInventory from a file.
     * You need to be sure that provided file only
     * contains a BlobInventory since in InventoryManager
     * there's a method that can load multiple inventories
     * inside a single File.
     * This way is a bit more efficient in some IDE's since after
     * typing BlobInventory and a dot, it will show you the
     * static methods, such as 'fromFile' and will also prompt
     * the file parameter you should already have.
     * <pre>
     *     BlobInventory inventory = new BlobInventory(file);
     *
     *     assert inventory.equals(BlobInventory.fromFile(file));
     *     </pre>
     *
     * @param file The file to load the inventory from.
     * @return The BlobInventory loaded from the file.
     */
    public static BlobInventory fromFile(File file) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(
                Objects.requireNonNull(file, "'file' cannot be null!"));
        String title = ChatColor.translateAlternateColorCodes('&',
                configuration.getString("Title", configuration.getName() + ">NOT-SET"));
        int size = configuration.getInt("Size", -1);
        if (size < 0 || size % 9 != 0) {
            if (size < 0) {
                size = 54;
                Bukkit.getLogger().info(configuration.getName() + "'s Size is smaller than 0.");
                Bukkit.getLogger().info("This was probably due because you never set a Size.");
                Bukkit.getLogger().info("This is not possible in an inventory so it was set");
                Bukkit.getLogger().info("to '54' which is default.");
            } else {
                size = 54;
                Bukkit.getLogger().info(configuration.getName() + "'s Size is not a factor of 9.");
                Bukkit.getLogger().info("This is not possible in an inventory so it was set");
                Bukkit.getLogger().info("to '54' which is default.");
            }
        }
        BlobButtonManager buttonManager = BlobButtonManager.fromConfigurationSection(configuration.getConfigurationSection("Buttons"));
        return new BlobInventory(title, size, buttonManager);
    }

    /**
     * Parses a BlobInventory from a ConfigurationSection.
     *
     * @param configurationSection The ConfigurationSection to parse from.
     * @return The BlobInventory parsed from the ConfigurationSection.
     * @deprecated Smart methods were made during development and are already
     * safe to use. Use {@link #fromConfigurationSection(ConfigurationSection)} instead
     * which is identical to this method.
     */
    @Deprecated
    public static BlobInventory smartFromConfigurationSection(ConfigurationSection configurationSection) {
        String title = ChatColor.translateAlternateColorCodes('&',
                Objects.requireNonNull(configurationSection,
                        "'configurationSection' cannot be null!").getString("Title", configurationSection.getName() + ">NOT-SET"));
        int size = configurationSection.getInt("Size", -1);
        if (size < 0 || size % 9 != 0) {
            if (size < 0) {
                size = 54;
                Bukkit.getLogger().info(configurationSection.getName() + "'s Size is smaller than 0.");
                Bukkit.getLogger().info("This was probably due because you never set a Size.");
                Bukkit.getLogger().info("This is not possible in an inventory so it was set");
                Bukkit.getLogger().info("to '54' which is default.");
            } else {
                size = 54;
                Bukkit.getLogger().info(configurationSection.getName() + "'s Size is not a factor of 9.");
                Bukkit.getLogger().info("This is not possible in an inventory so it was set");
                Bukkit.getLogger().info("to '54' which is default.");
            }
        }
        BlobButtonManager buttonManager = BlobButtonManager.smartFromConfigurationSection(configurationSection.getConfigurationSection("Buttons"));
        return new BlobInventory(title, size, buttonManager);
    }

    /**
     * Parses a BlobInventory from a ConfigurationSection.
     *
     * @param configurationSection The ConfigurationSection to parse from.
     * @return The BlobInventory parsed from the ConfigurationSection.
     */
    public static BlobInventory fromConfigurationSection(ConfigurationSection configurationSection) {
        String title = ChatColor.translateAlternateColorCodes('&',
                Objects.requireNonNull(configurationSection,
                        "'configurationSection' cannot be null!").getString("Title", configurationSection.getName() + ">NOT-SET"));
        int size = configurationSection.getInt("Size", -1);
        if (size < 0 || size % 9 != 0) {
            if (size < 0) {
                size = 54;
                Bukkit.getLogger().info(configurationSection.getName() + "'s Size is smaller than 0.");
                Bukkit.getLogger().info("This was probably due because you never set a Size.");
                Bukkit.getLogger().info("This is not possible in an inventory so it was set");
                Bukkit.getLogger().info("to '54' which is default.");
            } else {
                size = 54;
                Bukkit.getLogger().info(configurationSection.getName() + "'s Size is not a factor of 9.");
                Bukkit.getLogger().info("This is not possible in an inventory so it was set");
                Bukkit.getLogger().info("to '54' which is default.");
            }
        }
        BlobButtonManager buttonManager = BlobButtonManager.fromConfigurationSection(configurationSection.getConfigurationSection("Buttons"));
        return new BlobInventory(title, size, buttonManager);
    }

    /**
     * Constructs a BlobInventory with a title, size, and a ButtonManager.
     *
     * @param title         The title of the inventory.
     * @param size          The size of the inventory.
     * @param buttonManager The ButtonManager that will be used to manage the buttons.
     */
    protected BlobInventory(String title, int size, ButtonManager buttonManager) {
        this.setTitle(Objects.requireNonNull(title,
                "'title' cannot be null!"));
        this.setSize(size);
        this.setButtonManager(Objects.requireNonNull(buttonManager,
                "'buttonManager' cannot be null!"));
        this.buildInventory();
        this.loadDefaultButtons();
    }

    /**
     * Parses a BlobInventory from a File.
     *
     * @param file The File to parse from.
     */
    public BlobInventory(File file) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        setTitle(ChatColor.translateAlternateColorCodes('&',
                configuration.getString("Title", configuration.getName() + ">NOT-SET")));
        setSize(configuration.getInt("Size", -1));
        int size = getSize();
        if (size < 0 || size % 9 != 0) {
            setSize(54);
            if (size < 0) {
                Bukkit.getLogger().info(configuration.getName() + "'s Size is smaller than 0.");
                Bukkit.getLogger().info("This was probably due because you never set a Size.");
            } else {
                Bukkit.getLogger().info(configuration.getName() + "'s Size is not a factor of 9.");
            }
            Bukkit.getLogger().info("This is not possible in an inventory so it was set");
            Bukkit.getLogger().info("to '54' which is default.");
        }
        setButtonManager(BlobButtonManager
                .fromConfigurationSection(configuration
                        .getConfigurationSection("Buttons")));
        this.buildInventory();
        this.loadDefaultButtons();
    }

    /**
     * Creates a new BlobInventory in an empty constructor.
     * <p>
     * I recommend you try constructing your BlobInventory
     * and get used to the API for a strong workflow.
     */
    public BlobInventory() {
    }

    /**
     * Clones itself to a new reference.
     *
     * @return The cloned inventory
     * @throws CloneNotSupportedException If the inventory cannot be cloned.
     */
    @Override
    public BlobInventory clone() throws CloneNotSupportedException {
        return (BlobInventory) super.clone();
    }

    /**
     * Adds a default button using the specified key.
     * Default buttons are meant to persist inventory
     * changes.
     *
     * @param key The key of the button
     */
    public void addDefaultButton(String key) {
        for (Integer i : getSlots(key)) {
            addDefaultButton(key, getButton(i));
            break;
        }
    }

    /**
     * Loads/reloads the default buttons.
     */
    public void loadDefaultButtons() {
        setDefaultButtons(new HashMap<>());
        getKeys().forEach(this::addDefaultButton);
    }

    /**
     * Adds a default button to the memory of the inventory.
     *
     * @param name The name of the button
     * @param item The ItemStack of the button
     */
    public void addDefaultButton(String name, ItemStack item) {
        defaultButtons.put(name, item);
    }

    /**
     * Searches for the ItemStack that is linked to said key.
     *
     * @param key The key of the button
     * @return The ItemStack
     */
    public ItemStack getDefaultButton(String key) {
        return defaultButtons.get(key);
    }

    /**
     * Searches for the ItemStack that is linked to said key.
     * The ItemStack is cloned and then returned.
     *
     * @param key The key of the button
     * @return The cloned ItemStack
     */
    public ItemStack cloneDefaultButton(String key) {
        return defaultButtons.get(key).clone();
    }

    /**
     * Retrieves the default buttons
     *
     * @return The default buttons
     */
    public HashMap<String, ItemStack> getDefaultButtons() {
        return defaultButtons;
    }

    /**
     * Sets the default buttons
     *
     * @param defaultButtons The default buttons to set
     */
    public void setDefaultButtons(HashMap<String, ItemStack> defaultButtons) {
        this.defaultButtons = defaultButtons;
    }

    /**
     * Builds the inventory since by default its reference is null
     */
    public void buildInventory() {
        inventory = Bukkit.createInventory(null, getSize(), getTitle());
        getButtonManager().getIntegerKeys().forEach((k, v) -> inventory.setItem(k, v));
    }

    /**
     * Retrieves the inventory
     *
     * @return The inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Sets the inventory
     *
     * @param inventory The inventory to set
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Sets the button at the specified slot
     *
     * @param slot      The slot to set the button at
     * @param itemStack The ItemStack to set the button to
     */
    public void setButton(int slot, ItemStack itemStack) {
        inventory.setItem(slot, itemStack);
    }

    /**
     * Refills all ItemStacks that belong to said key
     *
     * @param key The key of the button
     */
    public void refillButton(String key) {
        for (Integer i : getSlots(key)) {
            setButton(i, getButton(i));
        }
    }
}
