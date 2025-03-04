package us.mytheria.bloblib.entities.translatable;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;
import us.mytheria.bloblib.entities.positionable.Positionable;

import java.util.Objects;

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
