package us.mytheria.bloblib.entities.currency;

import org.bson.Document;
import us.mytheria.bloblib.entities.BlobCrudable;

import java.util.ArrayList;
import java.util.List;

public interface WalletOwner {
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
     * Should not interact with the wallet directly, use the methods below.
     *
     * @return the wallet
     */
    Wallet getWallet();

    /**
     * Deposits the amount into the wallet.
     * <p>
     * Should not interact with the wallet directly, use the methods below.
     * </p>
     *
     * @param currency the currency
     * @param amount   the amount
     */
    default void deposit(Currency currency, double amount) {
        deposit(currency.getKey(), amount);
    }

    /**
     * Deposits the amount into the wallet.
     * <p>
     * Should not interact with the wallet directly, use the methods below.
     * </p>
     *
     * @param key    the key
     * @param amount the amount
     */
    default void deposit(String key, double amount) {
        getWallet().add(key, amount);
    }

    /**
     * Withdraws the amount from the wallet.
     * <p>
     * Should not interact with the wallet directly, use the methods below.
     * </p>
     *
     * @param currency the currency
     * @param amount   the amount
     */
    default void withdraw(Currency currency, double amount) {
        withdraw(currency.getKey(), amount);
    }

    /**
     * Withdraws the amount from the wallet.
     * <p>
     * Should not interact with the wallet directly, use the methods below.
     * </p>
     *
     * @param key    the key
     * @param amount the amount
     */
    default void withdraw(String key, double amount) {
        getWallet().subtract(key, amount);
    }

    /**
     * Checks if the wallet has the amount.
     * <p>
     * Should not interact with the wallet directly, use the methods below.
     * </p>
     *
     * @param currency the currency
     * @param amount   the amount
     * @return true if the wallet has the amount
     */
    default boolean has(Currency currency, double amount) {
        return has(currency.getKey(), amount);
    }

    /**
     * Checks if the wallet has the amount.
     * <p>
     * Should not interact with the wallet directly, use the methods below.
     * </p>
     *
     * @param key    the key
     * @param amount the amount
     * @return true if the wallet has the amount
     */
    default boolean has(String key, double amount) {
        return getWallet().has(key, amount);
    }

    /**
     * Gets the balance of the wallet.
     * <p>
     * Should not interact with the wallet directly, use the methods below.
     * </p>
     *
     * @param currency the currency
     * @return the balance
     */
    default double getBalance(Currency currency) {
        return getBalance(currency.getKey());
    }

    /**
     * Gets the balance of the wallet.
     * <p>
     * Should not interact with the wallet directly, use the methods below.
     * </p>
     *
     * @param key the key
     * @return the balance
     */
    default double getBalance(String key) {
        return getWallet().balance(key);
    }

    /**
     * Sets the balance of the wallet.
     * <p>
     * Should not interact with the wallet directly, use the methods below.
     * </p>
     *
     * @param currency the currency
     * @param amount   the amount
     */
    default void setBalance(Currency currency, double amount) {
        setBalance(currency.getKey(), amount);
    }

    /**
     * Sets the balance of the wallet.
     * <p>
     * Should not interact with the wallet directly, use the methods below.
     * </p>
     *
     * @param key    the key
     * @param amount the amount
     */
    default void setBalance(String key, double amount) {
        getWallet().put(key, amount);
    }

    /**
     * Resets the wallet to the initial balance.
     * <p>
     * Should not interact with the wallet directly, use the methods below.
     * </p>
     *
     * @param currency the currency
     */
    default void reset(Currency currency) {
        setBalance(currency.getKey(), currency.getInitialBalance());
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