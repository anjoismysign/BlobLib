package io.github.anjoismysign.bloblib.inventory;

import io.github.anjoismysign.bloblib.domain.Localizable;
import io.github.anjoismysign.bloblib.utility.TextColor;
import io.github.anjoismysign.holoworld.asset.DataAsset;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record InventoryBuilderCarrier<T extends InventoryButton>(@NotNull String title,
                                                                 int size,
                                                                 @NotNull ButtonManager<T> buttonManager,
                                                                 @Nullable String type,
                                                                 @NotNull String reference,
                                                                 @NotNull String locale,
                                                                 @NotNull String path) implements DataAsset, Localizable {
    @Override
    @NotNull
    public String identifier() {
        return reference;
    }

    public boolean isMetaInventoryButton() {
        return type != null;
    }

    @NotNull
    public static InventoryBuilderCarrier<InventoryButton> BLOB_FROM_CONFIGURATION_SECTION(
            @NotNull ConfigurationSection configurationSection,
            @NotNull String reference,
            @NotNull String path) {
        Objects.requireNonNull(configurationSection, "'configurationSection' cannot be null!");
        String title = TextColor.PARSE(configurationSection.getString("Title", configurationSection.getName() + ">NOT-SET"));
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
                .fromConfigurationSection(buttonsSection, locale, reference);
        return new InventoryBuilderCarrier<>(title, size, buttonManager,
                null, reference, locale, path);
    }

    public static InventoryBuilderCarrier<MetaInventoryButton> META_FROM_CONFIGURATION_SECTION(
            @NotNull ConfigurationSection configurationSection,
            @NotNull String reference,
            @NotNull String path) {
        Objects.requireNonNull(configurationSection, "'configurationSection' cannot be null!");
        String title = TextColor.PARSE(configurationSection.getString("Title", configurationSection.getName() + ">NOT-SET"));
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
                .fromConfigurationSection(buttonsSection, locale, reference);
        return new InventoryBuilderCarrier<>(title, size, buttonManager,
                type, reference, locale, path);
    }

    public InventoryBuilderCarrier<T> setLocale(@NotNull String locale) {
        Objects.requireNonNull(locale, "'locale' cannot be null!");
        return new InventoryBuilderCarrier<>(title, size, buttonManager, type, reference, locale, path);
    }

}
