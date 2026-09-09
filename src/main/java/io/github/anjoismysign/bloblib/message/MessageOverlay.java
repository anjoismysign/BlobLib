package io.github.anjoismysign.bloblib.message;

import io.github.anjoismysign.bloblib.content.LocaleOverlay;
import io.github.anjoismysign.bloblib.domain.DataAssetType;
import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import io.github.anjoismysign.bloblib.manager.LocalizableDataAssetManager;
import io.github.anjoismysign.bloblib.utility.TextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * The locale overlay of a BlobMessage: a non default locale file that carries the
 * message's text alone, inheriting its sound and its title timings from the en_us
 * file of the same reference.
 * <p>
 * Text is replaced, never merged. An overlay shows exactly what it declares, so a
 * locale may carry only an actionbar where en_us carries a chat line and a title.
 * This is the rule a TranslatableItem overlay follows.
 */
public enum MessageOverlay implements LocalizableDataAssetManager.OverlayHandler<BlobMessage> {
    INSTANCE;

    /**
     * The fields an overlay is read for. Everything else belongs to the en_us file.
     */
    public static final Set<String> TEXT_FIELDS = Set.of("Chat", "Hover", "Actionbar", "Title", "Subtitle");

    /**
     * @param section The section to inspect
     * @return true if the section declares any text at all
     */
    public static boolean declaresText(@NotNull ConfigurationSection section) {
        for (String field : TEXT_FIELDS) {
            if (section.isString(field)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void validate(@NotNull ConfigurationSection section,
                         @NotNull String locale,
                         @NotNull String reference,
                         @NotNull String filePath) {
        if (!declaresText(section)) {
            throw new ConfigurationFieldException("'" + reference + "' declares none of 'Chat', 'Hover', " +
                    "'Actionbar', 'Title' or 'Subtitle', so it would translate nothing. A '" + locale + "' file " +
                    "carries translatable text alone: the sound and the title timings are inherited from the " +
                    "en_us file and are ignored here. This file is rejected on purpose, because a translation " +
                    "that translates nothing is almost always a mistake. Either add text to translate, or " +
                    "delete the file.");
        }
    }

    @Override
    @NotNull
    public BlobMessage merge(@Nullable BlobMessage baseAsset,
                             @NotNull ConfigurationSection baseSection,
                             @NotNull ConfigurationSection section,
                             @NotNull String locale,
                             @NotNull String reference,
                             @NotNull String filePath) {
        if (baseAsset == null) {
            throw new ConfigurationFieldException("No default locale (" + LocaleOverlay.DEFAULT_LOCALE +
                    ") provided for '" + reference + "' BlobMessage");
        }
        LocaleOverlay.warnChangedFields(DataAssetType.BLOB_MESSAGE, reference, locale, filePath,
                section, baseSection, TEXT_FIELDS);
        return baseAsset.toModernMessage().overlay(locale,
                read(section, "Chat"),
                read(section, "Hover"),
                read(section, "Actionbar"),
                read(section, "Title"),
                read(section, "Subtitle"));
    }

    @Nullable
    private String read(@NotNull ConfigurationSection section,
                        @NotNull String field) {
        @Nullable String value = section.getString(field);
        return value == null ? null : TextColor.PARSE(value);
    }
}
