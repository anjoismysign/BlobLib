package io.github.anjoismysign.bloblib.content;

import io.github.anjoismysign.bloblib.domain.DataAssetType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Shared rules for locale overlays: files of a non-default locale that carry
 * translatable text alone, while every other field is inherited from the default
 * locale file of the same reference.
 */
public final class LocaleOverlay {

    /**
     * The locale whose files hold the non-translatable data of every asset.
     */
    public static final String DEFAULT_LOCALE = "en_us";

    /**
     * The key every asset file may carry to declare its locale.
     */
    public static final String LOCALE_FIELD = "Locale";

    private LocaleOverlay() {
    }

    /**
     * @param locale The locale to test
     * @return true if the locale is the default one, and so holds non-translatable data.
     */
    public static boolean isDefault(@NotNull String locale) {
        return locale.equalsIgnoreCase(DEFAULT_LOCALE);
    }

    /**
     * Registers a {@link ContentWarning} for every field of the section that a
     * locale overlay is not read for, so that editing a field which changes
     * nothing does not go unnoticed.
     *
     * @param type      The type of data asset
     * @param reference The identifier of the asset
     * @param locale    The locale of the overlay file
     * @param filePath  The path of the overlay file
     * @param section   The section to inspect
     * @param effective The fields the overlay is actually read for
     */
    public static void warnStrayFields(@NotNull DataAssetType type,
                                       @NotNull String reference,
                                       @NotNull String locale,
                                       @NotNull String filePath,
                                       @NotNull ConfigurationSection section,
                                       @NotNull Set<String> effective) {
        ContentWarningRegistry registry = ContentWarningRegistry.getInstance();
        for (String field : section.getKeys(false)) {
            if (field.equals(LOCALE_FIELD) || effective.contains(field))
                continue;
            registry.register(ContentWarning.noEffectInOverlay(type, reference, locale, filePath, field));
        }
    }
}
