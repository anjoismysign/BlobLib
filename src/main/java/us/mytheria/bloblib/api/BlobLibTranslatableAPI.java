package us.mytheria.bloblib.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.translatable.TranslatableBlock;
import us.mytheria.bloblib.entities.translatable.TranslatableItem;
import us.mytheria.bloblib.entities.translatable.TranslatableSnippet;
import us.mytheria.bloblib.managers.BlobLibConfigManager;
import us.mytheria.bloblib.managers.TranslatableManager;

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
     * Will get a TranslatableKey by its key and the default locale.
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
