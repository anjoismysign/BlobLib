package io.github.anjoismysign.bloblib.translatable;

import io.github.anjoismysign.bloblib.content.LocaleOverlay;
import io.github.anjoismysign.bloblib.domain.DataAssetType;
import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import io.github.anjoismysign.bloblib.middleman.itemstack.ItemStackReader;
import io.github.anjoismysign.bloblib.middleman.itemstack.OmniStack;
import io.github.anjoismysign.bloblib.utility.TextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TranslatableReader {

    private static final Set<String> ITEM_OVERLAY_ROOT_FIELDS = Set.of("ItemStack");
    private static final Set<String> ITEM_OVERLAY_STACK_FIELDS = Set.of("DisplayName", "ItemName", "Lore", "minimessage");

    public static TranslatableItem ITEM(@NotNull ConfigurationSection section,
                                        @NotNull String locale,
                                        @NotNull String key) {
        return ITEM(section, locale, key, section.getCurrentPath());
    }

    public static TranslatableItem ITEM(@NotNull ConfigurationSection section,
                                        @NotNull String locale,
                                        @NotNull String key,
                                        @NotNull String filePath) {
        Objects.requireNonNull(section, "Section cannot be null");
        Objects.requireNonNull(locale, "Locale cannot be null");
        Objects.requireNonNull(key, "Key cannot be null");
        if (!section.isConfigurationSection("ItemStack"))
            throw new ConfigurationFieldException("'ItemStack' is missing or not set");
        ConfigurationSection itemStackSection = section.getConfigurationSection("ItemStack");
        if (!LocaleOverlay.isDefault(locale))
            return OVERLAY_ITEM(section, itemStackSection, locale, key, filePath);
        boolean isSoul = section.getBoolean("Is-Soul", false);
        boolean isUnique = section.getBoolean("Is-Unique", false);
        String rarityName = section.getString("Rarity", "common");
        @Nullable Double fluidPressure = section.isDouble("Fluid-Pressure") ? section.getDouble("Fluid-Pressure") : null;
        @Nullable Double projectileDamage = section.isDouble("Projectile-Damage") ? section.getDouble("Projectile-Damage") : null;
        OmniStack omniStack = ItemStackReader.OMNI_STACK(itemStackSection, key);
        return BlobTranslatableItem.of(key, locale, omniStack, rarityName,
                isSoul, isUnique, fluidPressure, projectileDamage);
    }

    /**
     * Reads a locale overlay of a TranslatableItem: a non-en_us file that carries
     * translatable text alone. Every other field is inherited from the en_us file,
     * so ItemStackReader is deliberately not run here, and 'Material' is not required.
     */
    private static TranslatableItem OVERLAY_ITEM(@NotNull ConfigurationSection section,
                                                 @NotNull ConfigurationSection itemStackSection,
                                                 @NotNull String locale,
                                                 @NotNull String key,
                                                 @NotNull String filePath) {
        @Nullable String displayName = itemStackSection.getString("DisplayName", null);
        @Nullable String itemName = itemStackSection.getString("ItemName", null);
        @Nullable List<String> lore = itemStackSection.isList("Lore") ? itemStackSection.getStringList("Lore") : null;
        if (displayName == null && itemName == null && lore == null)
            throw new ConfigurationFieldException("'" + key + "' declares none of 'DisplayName', 'ItemName' or 'Lore', " +
                    "so it would translate nothing. A '" + locale + "' file carries translatable text alone: every other " +
                    "field is inherited from the en_us file and is ignored here. This file is rejected on purpose, " +
                    "because a translation that translates nothing is almost always a mistake. Either add one of those " +
                    "three fields, or delete the file.");
        @Nullable Boolean miniMessage = itemStackSection.isBoolean("minimessage") ? itemStackSection.getBoolean("minimessage") : null;
        LocaleOverlay.warnStrayFields(DataAssetType.TRANSLATABLE_ITEM, key, locale, filePath, section, ITEM_OVERLAY_ROOT_FIELDS);
        LocaleOverlay.warnStrayFields(DataAssetType.TRANSLATABLE_ITEM, key, locale, filePath, itemStackSection, ITEM_OVERLAY_STACK_FIELDS);
        return TranslatableItem.forLocale(key, locale, displayName, itemName, lore, miniMessage);
    }

    public static TranslatableSnippet SNIPPET(@NotNull ConfigurationSection section,
                                              @NotNull String locale,
                                              @NotNull String key) {
        Objects.requireNonNull(section, "Section cannot be null");
        Objects.requireNonNull(locale, "Locale cannot be null");
        Objects.requireNonNull(key, "Key cannot be null");
        if (!section.isString("Snippet"))
            throw new ConfigurationFieldException("'Snippet' field is required for TranslatableSnippets at " + section.getCurrentPath());
        String snippet = Objects.requireNonNull(section.getString("Snippet"));
        snippet = TextColor.PARSE(snippet);
        return BlobTranslatableSnippet.of(key, locale, snippet);
    }

    public static TranslatableBlock BLOCK(@NotNull ConfigurationSection section,
                                          @NotNull String locale,
                                          @NotNull String key) {
        Objects.requireNonNull(section, "Section cannot be null");
        Objects.requireNonNull(locale, "Locale cannot be null");
        Objects.requireNonNull(key, "Key cannot be null");
        List<String> lines = section.getStringList("Block");
        if (lines.isEmpty())
            throw new ConfigurationFieldException("'Block' field is required for TranslatableBlocks");
        lines = lines.stream().map(TextColor::PARSE).toList();
        return BlobTranslatableBlock.of(key, locale, lines);
    }
}
