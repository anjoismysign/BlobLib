package io.github.anjoismysign.bloblib;

import io.github.anjoismysign.bloblib.api.BlobLibDisguiseAPI;
import io.github.anjoismysign.bloblib.api.BlobLibEconomyAPI;
import io.github.anjoismysign.bloblib.api.BlobLibHologramAPI;
import io.github.anjoismysign.bloblib.api.BlobLibListenerAPI;
import io.github.anjoismysign.bloblib.api.BlobLibPermissionAPI;
import io.github.anjoismysign.bloblib.api.BlobLibPetAPI;
import io.github.anjoismysign.bloblib.api.BlobLibProfileAPI;

/**
 * @author anjoismysign
 * This class provides methods to interact with the BlobLib API.
 * It's not meant to change now that it follows the singleton pattern.
 */
public class BlobLibAPI {
    private static BlobLibAPI instance;
    private final BlobLibListenerAPI listenerAPI;
    private final BlobLibEconomyAPI economyAPI;
    private final BlobLibHologramAPI hologramAPI;
    private final BlobLibPermissionAPI permissionAPI;
    private final BlobLibDisguiseAPI disguiseAPI;
    private final BlobLibAssetAPI assetAPI;
    private final BlobLibPetAPI petAPI;
    private final BlobLibProfileAPI profileAPI;

    private BlobLibAPI(BlobLib plugin) {
        this.listenerAPI = BlobLibListenerAPI.getInstance(plugin);
        this.economyAPI = BlobLibEconomyAPI.getInstance(plugin);
        this.hologramAPI = BlobLibHologramAPI.getInstance(plugin);
        this.permissionAPI = BlobLibPermissionAPI.getInstance(plugin);
        this.disguiseAPI = BlobLibDisguiseAPI.getInstance(plugin);
        this.assetAPI = BlobLibAssetAPI.getInstance(plugin);
        this.petAPI = BlobLibPetAPI.getInstance(plugin);
        this.profileAPI = BlobLibProfileAPI.getInstance();
    }

    public static BlobLibAPI getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibAPI.instance = new BlobLibAPI(plugin);
        }
        return instance;
    }

    public static BlobLibAPI getInstance() {
        return getInstance(null);
    }

    public BlobLibListenerAPI getListenerAPI() {
        return listenerAPI;
    }

    public BlobLibEconomyAPI getEconomyAPI() {
        return economyAPI;
    }

    public BlobLibHologramAPI getHologramAPI() {
        return hologramAPI;
    }

    public BlobLibPermissionAPI getPermissionAPI() {
        return permissionAPI;
    }

    public BlobLibDisguiseAPI getDisguiseAPI() {
        return disguiseAPI;
    }

    public BlobLibAssetAPI getAssetAPI() {
        return assetAPI;
    }

    public BlobLibPetAPI getPetAPI() {
        return petAPI;
    }

    public BlobLibProfileAPI getProfileAPI() {
        return profileAPI;
    }
}
