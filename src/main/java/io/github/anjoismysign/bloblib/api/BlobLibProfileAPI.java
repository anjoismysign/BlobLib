package io.github.anjoismysign.bloblib.api;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.vault.profile.ElasticProfile;
import org.jetbrains.annotations.NotNull;

public class BlobLibProfileAPI {

    private static BlobLibProfileAPI INSTANCE;

    public static BlobLibProfileAPI getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BlobLibProfileAPI();
        }
        return INSTANCE;
    }

    private BlobLibProfileAPI() {
    }

    /**
     * Gets the detected Vault profile provider, wrapped in an {@link ElasticProfile}.
     * If there is no Vault compatible profile provider, the returned instance
     * is absent ({@link ElasticProfile#isAbsent()} returns true).
     *
     * @return the ElasticProfile that has been detected.
     */
    @NotNull
    public ElasticProfile getProvider() {
        return BlobLib.getInstance().getVaultManager().getElasticProfile();
    }

}
