package us.mytheria.bloblib.entities;

import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.entities.currency.WalletHolder;

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
