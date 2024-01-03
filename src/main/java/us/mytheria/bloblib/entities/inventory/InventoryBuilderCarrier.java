package us.mytheria.bloblib.entities.inventory;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.utilities.TextColor;

import java.io.File;
import java.util.Objects;

public record InventoryBuilderCarrier<T extends InventoryButton>(@Nullable String title,
                                                                 int size,
                                                                 @NotNull ButtonManager<T> buttonManager,
                                                                 @Nullable String type,
                                                                 @NotNull String reference,
                                                                 @NotNull String locale) {
    public boolean isMetaInventoryButton() {
        return type != null;
    }

    public static InventoryBuilderCarrier<InventoryButton> BLOB_FROM_FILE(@NotNull File file) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(
                Objects.requireNonNull(file, "'file' cannot be null!"));
        String fileName = FilenameUtils.removeExtension(file.getName());
        return BLOB_FROM_CONFIGURATION_SECTION(configuration, fileName);
    }

    public static InventoryBuilderCarrier<InventoryButton> BLOB_FROM_CONFIGURATION_SECTION(
            @NotNull ConfigurationSection configurationSection, @NotNull String reference) {
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
        String locale = configurationSection.getString("Locale", "en_us");
        ConfigurationSection buttonsSection = configurationSection
                .getConfigurationSection("Buttons");
        if (buttonsSection == null)
            buttonsSection = configurationSection.createSection("Buttons");
        BlobButtonManager buttonManager = BlobButtonManager
                .fromConfigurationSection(buttonsSection, locale);
        return new InventoryBuilderCarrier<>(title, size, buttonManager,
                null, reference, locale);
    }

    public static InventoryBuilderCarrier<MetaInventoryButton> META_FROM_FILE(@NotNull File file) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(
                Objects.requireNonNull(file, "'file' cannot be null!"));
        String fileName = FilenameUtils.removeExtension(file.getName());
        return META_FROM_CONFIGURATION_SECTION(configuration, fileName);
    }

    public static InventoryBuilderCarrier<MetaInventoryButton> META_FROM_CONFIGURATION_SECTION(
            @NotNull ConfigurationSection configurationSection, @NotNull String reference) {
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
        String type = configurationSection.isString("Type")
                ? configurationSection.getString("Type") : "DEFAULT";
        String locale = configurationSection.getString("Locale", "en_us");
        ConfigurationSection buttonsSection = configurationSection
                .getConfigurationSection("Buttons");
        if (buttonsSection == null)
            buttonsSection = configurationSection.createSection("Buttons");
        MetaBlobButtonManager buttonManager = MetaBlobButtonManager
                .fromConfigurationSection(buttonsSection, locale);
        return new InventoryBuilderCarrier<>(title, size, buttonManager,
                type, reference, locale);
    }
}
