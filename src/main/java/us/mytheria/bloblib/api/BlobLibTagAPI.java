package us.mytheria.bloblib.api;

import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.tag.TagSet;
import us.mytheria.bloblib.managers.DataAssetManager;

public class BlobLibTagAPI {
    private static BlobLibTagAPI instance;
    private final BlobLib plugin;

    private BlobLibTagAPI(BlobLib plugin) {
        this.plugin = plugin;
    }

    public static BlobLibTagAPI getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibTagAPI.instance = new BlobLibTagAPI(plugin);
        }
        return instance;
    }

    public static BlobLibTagAPI getInstance() {
        return getInstance(null);
    }

    /**
     * @return The TagSet manager
     */
    public DataAssetManager<TagSet> getTagSetManager() {
        return plugin.getTagSetManager();
    }

    /**
     * @param key The key of the TagSet
     * @return The TagSet. Null if not found.
     */
    @Nullable
    public TagSet getTagSet(String key) {
        return getTagSetManager().getAsset(key);
    }
}
