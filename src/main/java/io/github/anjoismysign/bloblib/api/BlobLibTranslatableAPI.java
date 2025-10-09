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
     * Will get a TranslatableBlock by its identifier and locale.
     * If locale is not available, it will return the default locale.
     *
     * @param identifier The identifier of the translatable
     * @param locale The locale of the translatable
     * @return The TranslatableBlock
     */
    @Nullable
    public TranslatableBlock getTranslatableBlock(@NotNull String identifier,
                                                  @NotNull String locale) {
        return getTranslatableManager().getBlock(identifier, locale);
    }

    /**
     * Will get a TranslatableBlock by its identifier and the default locale.
     *
     * @param identifier The identifier of the translatable
     * @return The TranslatableBlock
     */
    @Nullable
    public TranslatableBlock getTranslatableBlock(@NotNull String identifier) {
        return getTranslatableManager().getBlock(identifier);
    }

    /**
     * Will get a TranslatableBlock by its identifier and the player's locale.
     *
     * @param identifier The identifier of the translatable
     * @param player The player to get the locale from
     * @return The TranslatableBlock
     */
    @Nullable
    public TranslatableBlock getTranslatableBlock(@NotNull String identifier,
                                                  @NotNull Player player) {
        Objects.requireNonNull(player);
        return getTranslatableManager().getBlock(identifier, player.getLocale());
    }

    /**
     * Will get a TranslatableSnippet by its identifier and locale.
     * If locale is not available, it will return the default locale.
     *
     * @param identifier The identifier of the translatable
     * @param locale The locale of the translatable
     * @return The TranslatableSnippet
     */
    @Nullable
    public TranslatableSnippet getTranslatableSnippet(@NotNull String identifier,
                                                      @NotNull String locale) {
        return getTranslatableManager().getSnippet(identifier, locale);
    }

    /**
     * Will get a TranslatableSnippet by its identifier and the default locale.
     *
     * @param identifier The identifier of the translatable
     * @return The TranslatableSnippet
     */
    @Nullable
    public TranslatableSnippet getTranslatableSnippet(@NotNull String identifier) {
        return getTranslatableManager().getSnippet(identifier);
    }

    /**
     * Will get a TranslatableSnippet by its identifier and the player's locale.
     *
     * @param identifier The identifier of the translatable
     * @param player The player to get the locale from
     * @return The TranslatableSnippet
     */
    @Nullable
    public TranslatableSnippet getTranslatableSnippet(@NotNull String identifier,
                                                      @NotNull Player player) {
        Objects.requireNonNull(player);
        return getTranslatableManager().getSnippet(identifier, player.getLocale());
    }

    /**
     * Will get a TranslatableItem by its identifier and locale.
     *
     * @param identifier The identifier of the translatable
     * @param locale The locale of the translatable
     * @return The TranslatableItem
     */
    @Nullable
    public TranslatableItem getTranslatableItem(@NotNull String identifier,
                                                @NotNull String locale) {
        return plugin.getTranslatableItemManager().getAsset(identifier, locale);
    }

    /**
     * Will get a TranslatableItem by its identifier and the default locale.
     *
     * @param identifier The identifier of the translatable
     * @return The TranslatableItem
     */
    @Nullable
    public TranslatableItem getTranslatableItem(@NotNull String identifier) {
        return plugin.getTranslatableItemManager().getAsset(identifier);
    }

    /**
     * Will get a TranslatableItem by its identifier and the player's locale.
     *
     * @param identifier The identifier of the translatable
     * @param player The player to get the locale from
     * @return The TranslatableItem
     */
    @Nullable
    public TranslatableItem getTranslatableItem(@NotNull String identifier,
                                                @NotNull Player player) {
        Objects.requireNonNull(player);
        return plugin.getTranslatableItemManager().getAsset(identifier, player.getLocale());
    }

    /**
     * Get all the TranslatableItems by their locale.
     *
     * @param locale The locale for the TranslatableItems
     * @return The list of TranslatableItem
     */
    @NotNull
    public List<TranslatableItem> getTranslatableItems(@NotNull String locale) {
        return plugin.getTranslatableItemManager().getAssets(locale);
    }

    /**
     * Will get a TranslatablePositionable by its identifier and locale.
     *
     * @param identifier The identifier of the translatable
     * @param locale The locale of the translatable
     * @return The TranslatablePositionable
     */
    @Nullable
    public TranslatablePositionable getTranslatablePositionable(@NotNull String identifier,
                                                                @NotNull String locale) {
        return plugin.getTranslatablePositionableManager().getAsset(identifier, locale);
    }

    /**
     * Will get a TranslatablePositionable by its identifier and the default locale.
     *
     * @param identifier The identifier of the translatable
     * @return The TranslatablePositionable
     */
    @Nullable
    public TranslatablePositionable getTranslatablePositionable(@NotNull String identifier) {
        return plugin.getTranslatablePositionableManager().getAsset(identifier);
    }

    /**
     * Will get a TranslatablePositionable by its identifier and the player's locale.
     *
     * @param identifier The identifier of the translatable
     * @param player The player to get the locale from
     * @return The TranslatablePositionable
     */
    @Nullable
    public TranslatablePositionable getTranslatablePositionable(@NotNull String identifier,
                                                                @NotNull Player player) {
        Objects.requireNonNull(player);
        return plugin.getTranslatablePositionableManager().getAsset(identifier, player.getLocale());
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
     * Will get a TranslatableArea by its identifier and locale.
     *
     * @param identifier The identifier of the translatable
     * @param locale The locale of the translatable
     * @return The TranslatableArea
     */
    @Nullable
    public TranslatableArea getTranslatableArea(@NotNull String identifier,
                                                @NotNull String locale) {
        return plugin.getTranslatableAreaManager().getAsset(identifier, locale);
    }

    /**
     * Will get a TranslatableArea by its identifier and the default locale.
     *
     * @param identifier The identifier of the translatable
     * @return The TranslatableArea
     */
    @Nullable
    public TranslatableArea getTranslatableArea(@NotNull String identifier) {
        return plugin.getTranslatableAreaManager().getAsset(identifier);
    }

    /**
     * Will get a TranslatableArea by its identifier and the player's locale.
     *
     * @param identifier The identifier of the translatable
     * @param player The player to get the locale from
     * @return The TranslatableArea
     */
    @Nullable
    public TranslatableArea getTranslatableArea(@NotNull String identifier,
                                                @NotNull Player player) {
        Objects.requireNonNull(player);
        return plugin.getTranslatableAreaManager().getAsset(identifier, player.getLocale());
    }

    /**
     * Get all the TranslatableAreas by their locale.
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
