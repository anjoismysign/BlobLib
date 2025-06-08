package us.mytheria.bloblib;

import us.mytheria.bloblib.api.BlobLibActionAPI;
import us.mytheria.bloblib.api.BlobLibInventoryAPI;
import us.mytheria.bloblib.api.BlobLibMessageAPI;
import us.mytheria.bloblib.api.BlobLibSoundAPI;
import us.mytheria.bloblib.api.BlobLibTagAPI;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;
import us.mytheria.bloblib.itemapi.ItemMaterialManager;

/**
 * Please use the API classes instead of this class.
 */
public class BlobLibAssetAPI {
    private static BlobLibAssetAPI instance;
    private final BlobLibSoundAPI soundAPI;
    private final BlobLibInventoryAPI inventoryAPI;
    private final BlobLibActionAPI actionAPI;
    private final BlobLibTagAPI tagAPI;
    private final BlobLibMessageAPI messageAPI;
    private final BlobLibTranslatableAPI translatableAPI;
    private final ItemMaterialManager materialManager;

    public static BlobLibAssetAPI getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibAssetAPI.instance = new BlobLibAssetAPI(plugin);
        }
        return instance;
    }

    public static BlobLibAssetAPI getInstance() {
        return getInstance(null);
    }

    private BlobLibAssetAPI(BlobLib plugin) {
        this.soundAPI = BlobLibSoundAPI.getInstance(plugin);
        this.inventoryAPI = BlobLibInventoryAPI.getInstance(plugin);
        this.actionAPI = BlobLibActionAPI.getInstance(plugin);
        this.tagAPI = BlobLibTagAPI.getInstance(plugin);
        this.messageAPI = BlobLibMessageAPI.getInstance(plugin);
        this.translatableAPI = BlobLibTranslatableAPI.getInstance(plugin);
        this.materialManager = new ItemMaterialManager() {
        };
    }

    public BlobLibSoundAPI getSoundAPI() {
        return soundAPI;
    }

    public BlobLibInventoryAPI getInventoryAPI() {
        return inventoryAPI;
    }

    public BlobLibActionAPI getActionAPI() {
        return actionAPI;
    }

    public BlobLibTagAPI getTagAPI() {
        return tagAPI;
    }

    public BlobLibMessageAPI getMessageAPI() {
        return messageAPI;
    }

    public BlobLibTranslatableAPI getTranslatableAPI() {
        return translatableAPI;
    }

    public ItemMaterialManager getMaterialManager() {
        return materialManager;
    }

}
