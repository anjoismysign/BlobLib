package io.github.anjoismysign.bloblib.content;

import io.github.anjoismysign.bloblib.domain.DataAssetType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A warning raised while loading content, pointing at something a server admin
 * or content creator wrote that BlobLib accepted but did not act upon.
 * <p>
 * Warnings are never fatal. They exist so that a field which silently has no
 * effect does not go unnoticed for weeks.
 *
 * @param type      The type of data asset the warning belongs to
 * @param reference The identifier of the asset
 * @param locale    The locale of the file the warning was found in
 * @param filePath  The path of the file the warning was found in
 * @param field     The offending field
 * @param reason    Why the field had no effect
 */
public record ContentWarning(@NotNull DataAssetType type,
                             @NotNull String reference,
                             @NotNull String locale,
                             @NotNull String filePath,
                             @NotNull String field,
                             @NotNull String reason) {

    public ContentWarning {
        Objects.requireNonNull(type, "'type' cannot be null");
        Objects.requireNonNull(reference, "'reference' cannot be null");
        Objects.requireNonNull(locale, "'locale' cannot be null");
        Objects.requireNonNull(filePath, "'filePath' cannot be null");
        Objects.requireNonNull(field, "'field' cannot be null");
        Objects.requireNonNull(reason, "'reason' cannot be null");
    }

    /**
     * Builds the warning raised when a locale overlay declares a field that only
     * the default locale (en_us) file is read for.
     *
     * @param type      The type of data asset
     * @param reference The identifier of the asset
     * @param locale    The locale of the overlay file
     * @param filePath  The path of the overlay file
     * @param field     The field that had no effect
     * @return The warning
     */
    @NotNull
    public static ContentWarning noEffectInOverlay(@NotNull DataAssetType type,
                                                   @NotNull String reference,
                                                   @NotNull String locale,
                                                   @NotNull String filePath,
                                                   @NotNull String field) {
        return new ContentWarning(type, reference, locale, filePath, field,
                "'" + field + "' has no effect in a '" + locale + "' file. Only the en_us file is read for it, " +
                        "since non-default locales carry translatable text alone.");
    }

    /**
     * @return A single line describing this warning, meant for a console log or a file report.
     */
    @NotNull
    public String asLine() {
        return type.name() + " '" + reference + "' (" + locale + "): " + reason;
    }
}
