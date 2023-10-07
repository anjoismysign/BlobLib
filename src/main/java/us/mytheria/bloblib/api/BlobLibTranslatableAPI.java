package us.mytheria.bloblib.api;

import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.translatable.TranslatableBlock;
import us.mytheria.bloblib.entities.translatable.TranslatableSnippet;
import us.mytheria.bloblib.managers.TranslatableManager;


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
    public TranslatableBlock getTranslatableBlock(String key, String locale) {
        return getTranslatableManager().getBlock(key, locale);
    }

    /**
     * Will get a TranslatableBlock by its key and the default locale.
     *
     * @param key The key of the translatable
     * @return The TranslatableBlock
     */
    @Nullable
    public TranslatableBlock getTranslatableBlock(String key) {
        return getTranslatableManager().getBlock(key);
    }

    /**
     * Will get a TranslatableSnippet by its key and locale.
     * If locale is not available, it will return the default locale.
     *
     * @param locale The locale of the translatable
     * @return The TranslatableSnippet
     */
    @Nullable
    public TranslatableSnippet getTranslatableSnippet(String key, String locale) {
        return getTranslatableManager().getSnippet(key, locale);
    }

    /**
     * Will get a TranslatableSnippet by its key and the default locale.
     *
     * @param key The key of the translatable
     * @return The TranslatableSnippet
     */
    @Nullable
    public TranslatableSnippet getTranslatableSnippet(String key) {
        return getTranslatableManager().getSnippet(key);
    }
}
