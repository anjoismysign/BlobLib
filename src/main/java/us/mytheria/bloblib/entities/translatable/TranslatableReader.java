package us.mytheria.bloblib.entities.translatable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.FluidPressureAPI;
import us.mytheria.bloblib.SoulAPI;
import us.mytheria.bloblib.UniqueAPI;
import us.mytheria.bloblib.exception.ConfigurationFieldException;
import us.mytheria.bloblib.itemstack.ItemStackReader;
import us.mytheria.bloblib.utilities.TextColor;

import java.util.List;
import java.util.Objects;

public class TranslatableReader {

    public static TranslatableItem ITEM(@NotNull ConfigurationSection section,
                                        @NotNull String locale,
                                        @NotNull String key) {
        Objects.requireNonNull(section, "Section cannot be null");
        Objects.requireNonNull(locale, "Locale cannot be null");
        Objects.requireNonNull(key, "Key cannot be null");
        if (!section.isConfigurationSection("ItemStack"))
            throw new ConfigurationFieldException("'ItemStack' is missing or not set");
        boolean isSoul = section.getBoolean("Is-Soul", false);
        boolean isUnique = section.getBoolean("Is-Unique", false);
        ItemStack itemStack = ItemStackReader.getInstance().readOrFailFast(section.getConfigurationSection("ItemStack"));
        if (isSoul)
            SoulAPI.getInstance().set(itemStack);
        if (isUnique)
            UniqueAPI.getInstance().set(itemStack);
        if (section.isDouble("Fluid-Pressure")) {
            double pressure = section.getDouble("Fluid-Pressure");
            FluidPressureAPI.getInstance().set(itemStack, pressure);
        }
        return BlobTranslatableItem.of(key, locale, itemStack);
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
