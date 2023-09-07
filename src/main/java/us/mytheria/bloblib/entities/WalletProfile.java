package us.mytheria.bloblib.entities;

import us.mytheria.bloblib.entities.currency.WalletHolder;

import java.util.Map;

public interface WalletProfile extends SerializableProfile, WalletHolder {
    /**
     * Will serialize the profile into a Map.
     *
     * @return the serialized profile
     */
    @Override
    default Map<String, Object> serialize() {
        return Map.of(
                "ProfileName", getProfileName(),
                "LastKnownName", getLastKnownName(),
                "UniqueId", getIdentification(),
                "Wallet", getWallet()
        );
    }
}
