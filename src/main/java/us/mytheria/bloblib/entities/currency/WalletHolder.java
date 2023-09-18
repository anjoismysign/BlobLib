package us.mytheria.bloblib.entities.currency;

public interface WalletHolder {
    /**
     * Should not interact with the wallet directly, use the methods below.
     *
     * @return the wallet
     */
    Wallet getWallet();

    /**
     * Deposits the amount into the wallet.
     * <p>
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
     *
     * @param currency the currency
     */
    default void reset(Currency currency) {
        setBalance(currency.getKey(), currency.getInitialBalance());
    }
}
