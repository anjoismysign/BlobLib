package us.mytheria.bloblib.entities.inventory;

import me.anjoismysign.anjo.entities.Result;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.utilities.TextColor;

import java.io.File;
import java.util.Objects;

public class MetaBlobInventory extends SharableInventory<MetaInventoryButton> {
    private final String type;

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
        return fromConfigurationSection(configuration);
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
        String type = "DEFAULT";
        if (configurationSection.isString("Type")) {
            type = configurationSection.getString("Type");
        }
        MetaBlobButtonManager buttonManager = MetaBlobButtonManager.fromConfigurationSection(configurationSection.getConfigurationSection("Buttons"));
        return new MetaBlobInventory(title, size, buttonManager, type);
    }

    public MetaBlobInventory(@NotNull String title, int size,
                             @NotNull ButtonManager<MetaInventoryButton> buttonManager,
                             @NotNull String type) {
        super(title, size, buttonManager);
        this.type = Objects.requireNonNull(type, "'type' cannot be null!");
    }

    @Override
    @NotNull
    public MetaBlobInventory copy() {
        return new MetaBlobInventory(getTitle(), getSize(), getButtonManager(), getType());
    }

    /**
     * Will filter between buttons and will check if they have a valid Meta.
     * First button from this filter to contain the provided slot will be returned
     * as a valid Result. An invalid Result will be returned if no candidate was found.
     *
     * @param slot The slot to check.
     * @return The button that has a valid Meta and contains provided slot.
     */
    @NotNull
    public Result<MetaInventoryButton> belongsToAMetaButton(int slot) {
        MetaInventoryButton metaInventoryButton =
                getKeys().stream().map(this::getButton).filter(MetaInventoryButton::hasMeta)
                        .filter(button -> button.getSlots().contains(slot)).findFirst()
                        .orElse(null);
        return Result.ofNullable(metaInventoryButton);
    }

    @NotNull
    public String getType() {
        return type;
    }
}
