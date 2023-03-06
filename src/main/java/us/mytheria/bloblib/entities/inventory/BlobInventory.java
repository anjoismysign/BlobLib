package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.utilities.TextColor;

import java.io.File;
import java.util.Objects;

public class BlobInventory extends SharableInventory<InventoryButton> {

    /**
     * A way to create a BlobInventory from a file.
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
        return fromConfigurationSection(configuration);
    }

    /**
     * Parses a BlobInventory from a ConfigurationSection.
     *
     * @param configurationSection The ConfigurationSection to parse from.
     * @return The BlobInventory parsed from the ConfigurationSection.
     */
    public static BlobInventory fromConfigurationSection(ConfigurationSection configurationSection) {
        String title = TextColor.PARSE(Objects.requireNonNull(configurationSection,
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

    public BlobInventory(@NotNull String title, int size,
                         @NotNull ButtonManager<InventoryButton> buttonManager) {
        super(title, size, buttonManager);
    }

    @Override
    @NotNull
    public BlobInventory copy() {
        return new BlobInventory(getTitle(), getSize(), getButtonManager());
    }
}
