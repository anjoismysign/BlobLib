package io.github.anjoismysign.bloblib.api;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableArea;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableBlock;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableItem;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatablePositionable;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableSnippet;
import io.github.anjoismysign.bloblib.managers.BlobLibConfigManager;
import io.github.anjoismysign.bloblib.managers.TranslatableManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;


public class BlobLibTranslatableAPI {
    private static BlobLibTranslatableAPI instance;
    private final BlobLib plugin;

    private BlobLibTranslatableAPI(BlobLib plugin) {
        this.plugin = plugin;
    }

    public static BlobLibTranslatableAPI getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibTranslatableAPI.instance = new BlobLibTranslatableAPI(plugin);
        }
        return instance;
    }

    public static BlobLibTranslatableAPI getInstance() {
        return getInstance(null);
    }

    /**
     * @return The translatable manager
     */
    public TranslatableManager getTranslatableManager() {
        return plugin.getTranslatableManager();
    }

    /**
     * Will get a TranslatableBlock by its key and locale.
     * If locale is not available, it will return the default locale.
     *
     * @param key    The key of the translatable
     * @param locale The locale of the translatable
     * @return The TranslatableBlock
     */
    @Nullable
    public TranslatableBlock getTranslatableBlock(@NotNull String key,
                                                  @NotNull String locale) {
        return getTranslatableManager().getBlock(key, locale);
    }

    /**
     * Will get a TranslatableBlock by its key and the default locale.
     *
     * @param key The key of the translatable
     * @return The TranslatableBlock
     */
    @Nullable
    public TranslatableBlock getTranslatableBlock(@NotNull String key) {
        return getTranslatableManager().getBlock(key);
    }

    /**
     * Will get a TranslatableBlock by its key and the player's locale.
     *
     * @param key    The key of the translatable
     * @param player The player to get the locale from
     * @return The TranslatableBlock
     */
    @Nullable
    public TranslatableBlock getTranslatableBlock(@NotNull String key,
                                                  @NotNull Player player) {
        Objects.requireNonNull(player);
        return getTranslatableManager().getBlock(key, player.getLocale());
    }

    /**
     * Will get a TranslatableSnippet by its key and locale.
     * If locale is not available, it will return the default locale.
     *
     * @param locale The locale of the translatable
     * @return The TranslatableSnippet
     */
    @Nullable
    public TranslatableSnippet getTranslatableSnippet(@NotNull String key,
                                                      @NotNull String locale) {
        return getTranslatableManager().getSnippet(key, locale);
    }

    /**
     * Will get a TranslatableSnippet by its key and the default locale.
     *
     * @param key The key of the translatable
     * @return The TranslatableSnippet
     */
    @Nullable
    public TranslatableSnippet getTranslatableSnippet(@NotNull String key) {
        return getTranslatableManager().getSnippet(key);
    }

    /**
     * Will get a TranslatableSnippet by its key and the player's locale.
     *
     * @param key    The key of the translatable
     * @param player The player to get the locale from
     * @return The TranslatableSnippet
     */
    @Nullable
    public TranslatableSnippet getTranslatableSnippet(@NotNull String key,
                                                      @NotNull Player player) {
        Objects.requireNonNull(player);
        return getTranslatableManager().getSnippet(key, player.getLocale());
    }

    /**
     * Will get a TranslatableItem by its key and locale.
     *
     * @param key    The key of the translatable
     * @param locale The locale of the translatable
     * @return The TranslatableItem
     */
    @Nullable
    public TranslatableItem getTranslatableItem(@NotNull String key,
                                                @NotNull String locale) {
        return plugin.getTranslatableItemManager().getAsset(key, locale);
    }

    /**
     * Will get a TranslatableItem by its key and the default locale.
     *
     * @param key The key of the translatable
     * @return The TranslatableItem
     */
    @Nullable
    public TranslatableItem getTranslatableItem(@NotNull String key) {
        return plugin.getTranslatableItemManager().getAsset(key);
    }

    /**
     * Will get a TranslatableItem by its key and the player's locale.
     *
     * @param key    The key of the translatable
     * @param player The player to get the locale from
     * @return The TranslatableItem
     */
    @Nullable
    public TranslatableItem getTranslatableItem(@NotNull String key,
                                                @NotNull Player player) {
        Objects.requireNonNull(player);
        return plugin.getTranslatableItemManager().getAsset(key, player.getLocale());
    }

    /**
     * Get all the TranslatableItems by their key.
     *
     * @param locale The locale for the TranslatableItems
     * @return The list of TranslatableItem
     */
    @NotNull
    public List<TranslatableItem> getTranslatableItems(@NotNull String locale) {
        return plugin.getTranslatableItemManager().getAssets(locale);
    }

    /**
     * Will get a TranslatablePositionable by its key and locale.
     *
     * @param key    The key of the translatable
     * @param locale The locale of the translatable
     * @return The TranslatablePositionable
     */
    @Nullable
    public TranslatablePositionable getTranslatablePositionable(@NotNull String key,
                                                                @NotNull String locale) {
        return plugin.getTranslatablePositionableManager().getAsset(key, locale);
    }

    /**
     * Will get a TranslatablePositionable by its key and the default locale.
     *
     * @param key The key of the translatable
     * @return The TranslatablePositionable
     */
    @Nullable
    public TranslatablePositionable getTranslatablePositionable(@NotNull String key) {
        return plugin.getTranslatablePositionableManager().getAsset(key);
    }

    /**
     * Will get a TranslatablePositionable by its key and the player's locale.
     *
     * @param key    The key of the translatable
     * @param player The player to get the locale from
     * @return The TranslatablePositionable
     */
    @Nullable
    public TranslatablePositionable getTranslatablePositionable(@NotNull String key,
                                                                @NotNull Player player) {
        Objects.requireNonNull(player);
        return plugin.getTranslatablePositionableManager().getAsset(key, player.getLocale());
    }

    /**
     * Get all the TranslatablePositionables by their key.
     *
     * @param locale The locale for the TranslatablePositionables
     * @return The list of TranslatablePositionable
     */
    @NotNull
    public List<TranslatablePositionable> getTranslatablePositionables(@NotNull String locale) {
        return plugin.getTranslatablePositionableManager().getAssets(locale);
    }

    /**
     * Will get a TranslatableArea by its key and locale.
     *
     * @param key    The key of the translatable
     * @param locale The locale of the translatable
     * @return The TranslatableArea
     */
    @Nullable
    public TranslatableArea getTranslatableArea(@NotNull String key,
                                                @NotNull String locale) {
        return plugin.getTranslatableAreaManager().getAsset(key, locale);
    }

    /**
     * Will get a TranslatableArea by its key and the default locale.
     *
     * @param key The key of the translatable
     * @return The TranslatableArea
     */
    @Nullable
    public TranslatableArea getTranslatableArea(@NotNull String key) {
        return plugin.getTranslatableAreaManager().getAsset(key);
    }

    /**
     * Will get a TranslatableArea by its key and the player's locale.
     *
     * @param key    The key of the translatable
     * @param player The player to get the locale from
     * @return The TranslatableArea
     */
    @Nullable
    public TranslatableArea getTranslatableArea(@NotNull String key,
                                                @NotNull Player player) {
        Objects.requireNonNull(player);
        return plugin.getTranslatableAreaManager().getAsset(key, player.getLocale());
    }

    /**
     * Get all the TranslatableAreas by their key.
     *
     * @param locale The locale for the TranslatableAreas
     * @return The list of TranslatableArea
     */
    @NotNull
    public List<TranslatableArea> getTranslatableAreas(@NotNull String locale) {
        return plugin.getTranslatableAreaManager().getAssets(locale);
    }

    /**
     * Will get the real locale from the locale.
     *
     * @param locale The locale to get the real locale from
     * @return The real locale
     */
    @NotNull
    public String getRealLocale(@NotNull String locale) {
        Objects.requireNonNull(locale);
        return BlobLibConfigManager.getInstance().getRealLocale(locale);
    }
}
