package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import us.mytheria.bloblib.utilities.TextColor;

import java.io.File;

public class MetaBlobInventory extends BlobInventory {

    public static MetaBlobInventory smartFromFile(File file) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        String title = TextColor.PARSE(configuration.getString("Title", configuration.getName() + ">NOT-SET"));
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
        MetaBlobInventory inventory = new MetaBlobInventory(title, size, buttonManager);
        return inventory;
    }

    public static MetaBlobInventory fromFile(File file) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        String title = TextColor.PARSE(configuration.getString("Title", configuration.getName() + ">NOT-SET"));
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
        MetaBlobInventory inventory = new MetaBlobInventory(title, size, buttonManager);
        return inventory;
    }

    public static MetaBlobInventory fromConfigurationSection(ConfigurationSection section) {
        String title = TextColor.PARSE(section.getString("Title", section.getName() + ">NOT-SET"));
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
        MetaBlobInventory inventory = new MetaBlobInventory(title, size, buttonManager);
        return inventory;
    }

    private MetaBlobInventory(String title, int size, ButtonManager buttonManager) {
        super(title, size, buttonManager);
    }

    @Override
    public SuperBlobButtonManager getButtonManager() {
        return (SuperBlobButtonManager) super.getButtonManager();
    }
}
