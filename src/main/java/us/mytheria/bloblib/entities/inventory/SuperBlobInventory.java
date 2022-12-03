package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class SuperBlobInventory extends BlobInventory {

    public static SuperBlobInventory smartFromFile(File file) {
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
        SuperBlobButtonManager buttonManager = SuperBlobButtonManager.smartFromConfigurationSection(configuration.getConfigurationSection("Buttons"));
        SuperBlobInventory inventory = new SuperBlobInventory(title, size, buttonManager);
        return inventory;
    }

    public static SuperBlobInventory fromFile(File file) {
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
        SuperBlobButtonManager buttonManager = SuperBlobButtonManager.fromConfigurationSection(configuration.getConfigurationSection("Buttons"));
        SuperBlobInventory inventory = new SuperBlobInventory(title, size, buttonManager);
        return inventory;
    }

    public static SuperBlobInventory smarFromConfigurationSection(ConfigurationSection section) {
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
        SuperBlobButtonManager buttonManager = SuperBlobButtonManager.smartFromConfigurationSection(section.getConfigurationSection("Buttons"));
        SuperBlobInventory inventory = new SuperBlobInventory(title, size, buttonManager);
        return inventory;
    }

    public static SuperBlobInventory fromConfigurationSection(ConfigurationSection section) {
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
        SuperBlobButtonManager buttonManager = SuperBlobButtonManager.fromConfigurationSection(section.getConfigurationSection("Buttons"));
        SuperBlobInventory inventory = new SuperBlobInventory(title, size, buttonManager);
        return inventory;
    }

    private SuperBlobInventory(String title, int size, ButtonManager buttonManager) {
        super(title, size, buttonManager);
    }
}
