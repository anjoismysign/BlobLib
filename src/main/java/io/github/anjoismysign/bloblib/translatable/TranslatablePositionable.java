package io.github.anjoismysign.bloblib.translatable;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.api.BlobLibTranslatableAPI;
import io.github.anjoismysign.bloblib.positionable.Positionable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Function;

public interface TranslatablePositionable extends Displayable<Positionable> {

    /**
     * Gets a TranslatablePositionable by its key. Key is the same as identifier.
     *
     * @param key The key to get the tag set by.
     * @return The TranslatablePositionable, or null if it doesn't exist.
     */
    @Nullable
    static TranslatablePositionable by(@NotNull String key) {
        Objects.requireNonNull(key);
        return BlobLibTranslatableAPI.getInstance().getTranslatablePositionable(key);
    }

    /**
     * Creates a locale overlay of a TranslatablePositionable: it carries only the
     * translatable display, while the Positionable itself is resolved lazily from
     * the default locale (en_us) asset of the same reference.
     *
     * @param reference The identifier of the asset.
     * @param locale    The locale this overlay belongs to.
     * @param display   The translated display.
     * @return The overlaying TranslatablePositionable.
     */
    @NotNull
    static TranslatablePositionable forLocale(@NotNull String reference,
                                              @NotNull String locale,
                                              @NotNull String display) {
        Objects.requireNonNull(reference, "'reference' cannot be null");
        Objects.requireNonNull(locale, "'locale' cannot be null");
        Objects.requireNonNull(display, "'display' cannot be null");
        return new TranslatablePositionable() {
            @Override
            public @NotNull String getDisplay() {
                return display;
            }

            @Override
            public @NotNull Positionable get() {
                return Objects.requireNonNull(BlobLib.getInstance().getTranslatablePositionableManager().getAsset(reference),
                        "No default locale (en_us) provided for '" + reference + "' TranslatablePositionable").get();
            }

            @Override
            public @NotNull Translatable<Positionable> modify(Function<String, String> function) {
                return new BlobTranslatablePositionable(reference, locale, function.apply(display), get());
            }

            @Override
            public @NonNull String identifier() {
                return reference;
            }

            @Override
            public @NotNull String locale() {
                return locale;
            }
        };
    }

    /**
     * Localizes the TranslatablePositionable to a specific locale.
     *
     * @param locale The locale to localize to.
     * @return The localized TranslatablePositionable.
     */
    @Nullable
    default TranslatablePositionable localize(@NotNull String locale) {
        Objects.requireNonNull(locale, "'locale' cannot be null");
        if (locale().equals(locale))
            return this;
        return BlobLibTranslatableAPI.getInstance()
                .getTranslatablePositionable(identifier(),
                        locale);
    }

    /**
     * Localizes the TranslatablePositionable to a specific player's locale.
     *
     * @param player The player to localize to.
     * @return The localized TranslatablePositionable.
     */
    default TranslatablePositionable localize(@NotNull Player player) {
        Objects.requireNonNull(player, "'player' cannot be null");
        return localize(player.getLocale());
    }

    /**
     * Will get the TranslatablePositionableModder for this TranslatablePositionable.
     *
     * @return The TranslatablePositionableModder.
     */
    default TranslatablePositionableModder modder() {
        return TranslatablePositionableModder.mod(this);
    }
}
