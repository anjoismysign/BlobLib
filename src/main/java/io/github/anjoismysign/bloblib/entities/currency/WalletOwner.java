package io.github.anjoismysign.bloblib.entities.currency;

import io.github.anjoismysign.bloblib.entities.BlobCrudable;
import io.github.anjoismysign.bloblib.entities.BlobSerializable;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface WalletOwner extends BlobSerializable, WalletHolder {
    String getPlayerName();

    String getPlayerUniqueId();

    /**
     * Each WalletOwner should hold a BlobCrudable which
     * will be used to serialize and deserialize not only
     * the wallet, but also any other attributes.
     * This is due to allowing the WalletOwner to be
     * updated with new attributes in the future
     * without having to change anything inside
     * the database.
     *
     * @return the BlobCrudable
     */
    @Override
    BlobCrudable blobCrudable();

    /**
     * Serializes the wallet into the BlobCrudable.
     */
    default void serializeWallet(@NotNull Wallet wallet,
                                 @NotNull String key) {
        wallet.serializeInDocument(blobCrudable().getDocument(), key);
    }

    /**
     * Reads the Wallet from the BlobCrudable
     * and returns it.
     *
     * @return the wallet
     */
    default Wallet deserializeWallet(@NotNull String key) {
        Document document = blobCrudable().getDocument();
        Wallet wallet = new Wallet();
        if (!document.containsKey(key))
            return wallet;
        List<String> list = document.getList(key, String.class, new ArrayList<>());
        if (list.isEmpty()) {
            return wallet;
        }
        list.forEach(string -> {
            String[] split = string.split(":");
            String currency = split[0];
            wallet.put(currency, Double.parseDouble(split[1]));
        });
        return wallet;
    }

    /**
     * Retrieves the UUID of the WalletOwner.
     *
     * @return the UUID
     */
    default String getIdentification() {
        return blobCrudable().getIdentification();
    }
}