package io.github.anjoismysign.bloblib.entities;

import io.github.anjoismysign.bloblib.entities.currency.WalletHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface WalletProfile extends SerializableProfile, WalletHolder {
    /**
     * Will serialize the profile into a Map.
     * Might want to serialize the wallet for
     * issues (such as MongoDB)
     *
     * @return the serialized profile
     */
    @Override
    @NotNull
    default Map<String, Object> serialize() {
        return Map.of(
                "ProfileName", getProfileName(),
                "LastKnownName", getLastKnownName(),
                "Identification", getIdentification(),
                "Wallet", getWallet().serialize()
        );
    }
}
