package io.github.anjoismysign.bloblib.entities.currency;

public interface BankHolder {
    /**
     * Should not interact with the bank directly, use the methods below.
     *
     * @return the bank
     */
    Wallet getBank();

    /**
     * Deposits the amount into the bank.
     * <p>
     *
     * @param currency the currency
     * @param amount   the amount
     */
    default void bankDeposit(Currency currency, double amount) {
        bankDeposit(currency.getKey(), amount);
    }

    /**
     * Deposits the amount into the bank.
     * <p>
     *
     * @param key    the key
     * @param amount the amount
     */
    default void bankDeposit(String key, double amount) {
        getBank().add(key, amount);
    }

    /**
     * Withdraws the amount from the bank.
     * <p>
     *
     * @param currency the currency
     * @param amount   the amount
     */
    default void bankWithdraw(Currency currency, double amount) {
        bankWithdraw(currency.getKey(), amount);
    }

    /**
     * Withdraws the amount from the bank.
     * <p>
     *
     * @param key    the key
     * @param amount the amount
     */
    default void bankWithdraw(String key, double amount) {
        getBank().subtract(key, amount);
    }

    /**
     * Checks if the bank has the amount.
     * <p>
     *
     * @param currency the currency
     * @param amount   the amount
     * @return true if the bank has the amount
     */
    default boolean bankHas(Currency currency, double amount) {
        return bankHas(currency.getKey(), amount);
    }

    /**
     * Checks if the bank has the amount.
     * <p>
     *
     * @param key    the key
     * @param amount the amount
     * @return true if the bank has the amount
     */
    default boolean bankHas(String key, double amount) {
        return getBank().has(key, amount);
    }

    /**
     * Gets the balance of the bank.
     * <p>
     *
     * @param currency the currency
     * @return the balance
     */
    default double bankGetBalance(Currency currency) {
        return bankGetBalance(currency.getKey());
    }

    /**
     * Gets the balance of the bank.
     * <p>
     *
     * @param key the key
     * @return the balance
     */
    default double bankGetBalance(String key) {
        return getBank().balance(key);
    }

    /**
     * Sets the balance of the bank.
     * <p>
     *
     * @param currency the currency
     * @param amount   the amount
     */
    default void bankSetBalance(Currency currency, double amount) {
        bankSetBalance(currency.getKey(), amount);
    }

    /**
     * Sets the balance of the bank.
     * <p>
     *
     * @param key    the key
     * @param amount the amount
     */
    default void bankSetBalance(String key, double amount) {
        getBank().put(key, amount);
    }

    /**
     * Resets the bank to the initial balance.
     * <p>
     *
     * @param currency the currency
     */
    default void bankReset(Currency currency) {
        bankSetBalance(currency.getKey(), currency.getInitialBalance());
    }
}
