package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;

public class BlobInventory extends InventoryBuilder {
    private Inventory inventory;
    private HashMap<String, ItemStack> defaultButtons;

    public static BlobInventory fromFile(File file) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
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
        BlobInventory inventory = new BlobInventory(title, size, buttonManager);
        return inventory;
    }

    public static BlobInventory fromConfigurationSection(ConfigurationSection section) {
        String title = ChatColor.translateAlternateColorCodes('&',
                section.getString("Title", section.getName() + ">NOT-SET"));
        int size = section.getInt("Size", -1);
        if (size < 0 || size % 9 != 0) {
            if (size < 0) {
                size = 54;
                Bukkit.getLogger().info(section.getName() + "'s Size is smaller than 0.");
                Bukkit.getLogger().info("This was probably due because you never set a Size.");
                Bukkit.getLogger().info("This is not possible in an inventory so it was set");
                Bukkit.getLogger().info("to '54' which is default.");
            } else {
                size = 54;
                Bukkit.getLogger().info(section.getName() + "'s Size is not a factor of 9.");
                Bukkit.getLogger().info("This is not possible in an inventory so it was set");
                Bukkit.getLogger().info("to '54' which is default.");
            }
        }
        BlobButtonManager buttonManager = BlobButtonManager.fromConfigurationSection(section.getConfigurationSection("Buttons"));
        BlobInventory inventory = new BlobInventory(title, size, buttonManager);
        return inventory;
    }

    public BlobInventory(String title, int size, ButtonManager buttonManager) {
        this.setTitle(title);
        this.setSize(size);
        this.setButtonManager(buttonManager);
        this.buildInventory();
        this.loadDefaultButtons();
    }

    public BlobInventory(File file) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        setTitle(ChatColor.translateAlternateColorCodes('&',
                configuration.getString("Title", configuration.getName() + ">NOT-SET")));
        setSize(configuration.getInt("Size", -1));
        int size = getSize();
        if (size < 0 || size % 9 != 0) {
            if (size < 0) {
                setSize(54);
                Bukkit.getLogger().info(configuration.getName() + "'s Size is smaller than 0.");
                Bukkit.getLogger().info("This was probably due because you never set a Size.");
                Bukkit.getLogger().info("This is not possible in an inventory so it was set");
                Bukkit.getLogger().info("to '54' which is default.");
            } else {
                setSize(54);
                Bukkit.getLogger().info(configuration.getName() + "'s Size is not a factor of 9.");
                Bukkit.getLogger().info("This is not possible in an inventory so it was set");
                Bukkit.getLogger().info("to '54' which is default.");
            }
        }
        setButtonManager(BlobButtonManager
                .fromConfigurationSection(configuration
                        .getConfigurationSection("Buttons")));
        this.buildInventory();
        this.loadDefaultButtons();
    }

    public BlobInventory() {
    }

    public void addDefaultButtons(String key) {
        for (Integer i : getSlots(key)) {
            addDefaultButton(key, getButton(i));
            break;
        }
    }

    public void loadDefaultButtons() {
        setDefaultButtons(new HashMap<>());
    }

    public void addDefaultButton(String name, ItemStack item) {
        defaultButtons.put(name, item);
    }

    public ItemStack getDefaultButton(String key) {
        return defaultButtons.get(key);
    }

    public ItemStack cloneDefaultButton(String key) {
        return defaultButtons.get(key).clone();
    }

    public HashMap<String, ItemStack> getDefaultButtons() {
        return defaultButtons;
    }

    public void setDefaultButtons(HashMap<String, ItemStack> defaultButtons) {
        this.defaultButtons = defaultButtons;
    }

    public void buildInventory() {
        inventory = Bukkit.createInventory(null, getSize(), getTitle());
        getButtonManager().getIntegerKeys().forEach((k, v) -> {
            inventory.setItem(k, v);
        });
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void setButton(int slot, ItemStack itemStack) {
        inventory.setItem(slot, itemStack);
    }

    public void refillButton(String key) {
        for (Integer i : getSlots(key)) {
            setButton(i, getButton(i));
            break;
        }
    }
}
