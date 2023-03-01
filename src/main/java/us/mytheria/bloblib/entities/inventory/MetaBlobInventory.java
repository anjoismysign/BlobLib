package us.mytheria.bloblib.entities.inventory;

import me.anjoismysign.anjo.entities.Result;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import us.mytheria.bloblib.utilities.TextColor;

import java.io.File;
import java.util.Objects;

public class MetaBlobInventory extends SharableInventory<MetaInventoryButton> {

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
    public static MetaBlobInventory fromFile(File file) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(
                Objects.requireNonNull(file, "'file' cannot be null!"));
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
        MetaBlobButtonManager buttonManager = MetaBlobButtonManager.fromConfigurationSection(configuration.getConfigurationSection("Buttons"));
        return new MetaBlobInventory(title, size, buttonManager);
    }

    /**
     * Parses a BlobInventory from a ConfigurationSection.
     *
     * @param configurationSection The ConfigurationSection to parse from.
     * @return The BlobInventory parsed from the ConfigurationSection.
     */
    public static MetaBlobInventory fromConfigurationSection(ConfigurationSection configurationSection) {
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
        MetaBlobButtonManager buttonManager = MetaBlobButtonManager.fromConfigurationSection(configurationSection.getConfigurationSection("Buttons"));
        return new MetaBlobInventory(title, size, buttonManager);
    }

    private MetaBlobInventory(String title, int size, MetaBlobButtonManager buttonManager) {
        super(title, size, buttonManager);
    }

    @Override
    public MetaBlobInventory copy() {
        return (MetaBlobInventory) super.copy();
    }

    /**
     * Checks if a slot belongs to a button.
     *
     * @param slot The slot to check.
     * @return The button that the slot belongs to.
     */
    public Result<MetaInventoryButton> belongsToAButton(int slot) {
        for (String key : getKeys()) {
            MetaInventoryButton button = getButton(key);
            if (!button.getSlots().contains(slot)) continue;
            return Result.valid(button);
        }
        return Result.invalidBecauseNull();
    }
}
