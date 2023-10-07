package us.mytheria.bloblib.entities.translatable;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.exception.ConfigurationFieldException;

import java.util.List;
import java.util.Objects;

public class TranslatableReader {

    public static TranslatableSnippet SNIPPET(@NotNull ConfigurationSection section) {
        return SNIPPET(section, "en_us");
    }

    public static TranslatableSnippet SNIPPET(@NotNull ConfigurationSection section,
                                              @NotNull String locale) {
        Objects.requireNonNull(section, "Section cannot be null");
        Objects.requireNonNull(locale, "Locale cannot be null");
        if (!section.isString("Snippet"))
            throw new ConfigurationFieldException("'Snippet' field is required for TranslatableSnippets at " + section.getCurrentPath());
        String snippet = Objects.requireNonNull(section.getString("Snippet"));
        return BlobTranslatableSnippet.of(locale, snippet);
    }

    public static TranslatableBlock BLOCK(@NotNull ConfigurationSection section) {
        return BLOCK(section, "en_us");
    }

    public static TranslatableBlock BLOCK(@NotNull ConfigurationSection section,
                                          @NotNull String locale) {
        Objects.requireNonNull(section, "Section cannot be null");
        Objects.requireNonNull(locale, "Locale cannot be null");
        List<String> lines = section.getStringList("Lines");
        return BlobTranslatableBlock.of(locale, lines);
    }
}
