package io.github.anjoismysign.bloblib.entities.translatable;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.api.BlobLibTranslatableAPI;
import io.github.anjoismysign.bloblib.entities.area.Area;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public interface TranslatableArea extends Displayable<Area> {

    /**
     * Gets a TranslatableArea by its key. Key is the same as identifier.
     *
     * @param key The key to get the tag set by.
     * @return The TranslatableArea, or null if it doesn't exist.
     */
    @Nullable
    static TranslatableArea by(@NotNull String key) {
        Objects.requireNonNull(key);
        return BlobLibTranslatableAPI.getInstance().getTranslatableArea(key);
    }

    @NotNull
    static TranslatableArea forLocale(@NotNull String reference,
                                      @NotNull String locale,
                                      @NotNull String display) {
        return new TranslatableArea() {
            @Override
            public @NotNull String getDisplay() {
                return display;
            }

            @Override
            public @NotNull Area get() {
                return Objects.requireNonNull(BlobLib.getInstance().getTranslatableAreaManager().getAsset(reference), "No default locale provided").get();
            }

            @Override
            public @NotNull Translatable<Area> modify(Function<String, String> function) {
                return new BlobTranslatableArea(reference, locale, function.apply(display), get());
            }

            @Override
            public String identifier() {
                return reference;
            }

            @Override
            public @NotNull String locale() {
                return locale;
            }
        };
    }

    /**
     * Localizes the TranslatableArea to a specific locale.
     *
     * @param locale The locale to localize to.
     * @return The localized TranslatableArea.
     */
    @Nullable
    default TranslatableArea localize(@NotNull String locale) {
        Objects.requireNonNull(locale, "'locale' cannot be null");
        if (locale().equals(locale))
            return this;
        return BlobLibTranslatableAPI.getInstance()
                .getTranslatableArea(identifier(),
                        locale);
    }

    /**
     * Localizes the TranslatableArea to a specific player's locale.
     *
     * @param player The player to localize to.
     * @return The localized TranslatableArea.
     */
    default TranslatableArea localize(@NotNull Player player) {
        Objects.requireNonNull(player, "'player' cannot be null");
        return localize(player.getLocale());
    }

    /**
     * Will get the TranslatableAreaModder for this TranslatableArea.
     *
     * @return The TranslatableAreaModder.
     */
    default TranslatableAreaModder modder() {
        return TranslatableAreaModder.mod(this);
    }
}
