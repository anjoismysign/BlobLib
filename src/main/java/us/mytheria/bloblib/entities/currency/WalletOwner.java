package us.mytheria.bloblib.entities.currency;

import org.bson.Document;
import us.mytheria.bloblib.entities.BlobCrudable;
import us.mytheria.bloblib.entities.BlobSerializable;

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
     * This method should write all the transient
     * attributes to the BlobCrudable that are
     * stored inside the WalletOwner.
     * An example of this would be calling
     * {@link WalletOwner#serializeWallet()}.
     * <p>
     * Note, it needs to return the updated BlobCrudable
     * so that it can be used in the next method.
     *
     * @return the updated BlobCrudable
     */
    @Override
    BlobCrudable serializeAllAttributes();

    /**
     * Serializes the wallet into the BlobCrudable.
     */
    default void serializeWallet() {
        getWallet().serializeInDocument(blobCrudable().getDocument());
    }

    /**
     * Reads the Wallet from the BlobCrudable
     * and returns it.
     *
     * @return the wallet
     */
    default Wallet deserializeWallet() {
        Document document = blobCrudable().getDocument();
        Wallet wallet = new Wallet();
        if (!document.containsKey("Wallet"))
            return wallet;
        List<String> list = document.getList("Wallet", String.class, new ArrayList<>());
        if (list.size() == 0)
            return wallet;
        list.forEach(string -> {
            String[] split = string.split(":");
            String key = split[0];
            wallet.put(key, Double.parseDouble(split[1]));
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