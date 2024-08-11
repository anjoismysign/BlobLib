package us.mytheria.bloblib.entities.translatable;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;
import us.mytheria.bloblib.entities.area.Area;

import java.util.Objects;

public interface TranslatableArea extends Translatable<Area> {

    /**
     * Gets a TranslatableArea by its key. Key is the same as getReference.
     *
     * @param key The key to get the tag set by.
     * @return The TranslatableArea, or null if it doesn't exist.
     */
    @Nullable
    static TranslatableArea by(@NotNull String key) {
        Objects.requireNonNull(key);
        return BlobLibTranslatableAPI.getInstance().getTranslatableArea(key);
    }

    /**
     * Gets the display of this TranslatableArea
     *
     * @return The name
     */
    @NotNull
    String getDisplay();

    /**
     * Localizes the TranslatableArea to a specific locale.
     *
     * @param locale The locale to localize to.
     * @return The localized TranslatableArea.
     */
    @Nullable
    default TranslatableArea localize(@NotNull String locale) {
        Objects.requireNonNull(locale, "'locale' cannot be null");
        if (getLocale().equals(locale))
            return this;
        return BlobLibTranslatableAPI.getInstance()
                .getTranslatableArea(getReference(),
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
