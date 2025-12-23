package io.github.anjoismysign.bloblib.entities.currency;

public interface BankAccount {

    /**
     * Deposits the amount into the bank.
     * <p>
     *
     * @param key    the key
     * @param amount the amount
     */
    void deposit(String key, double amount);

    /**
     * Withdraws the amount from the bank.
     * <p>
     *
     * @param key    the key
     * @param amount the amount
     */
    void withdraw(String key, double amount);

    /**
     * Checks if the bank has the amount.
     * <p>
     *
     * @param key    the key
     * @param amount the amount
     * @return true if the bank has the amount
     */
    boolean has(String key, double amount);

    /**
     * Gets the balance of the bank.
     * <p>
     *
     * @param key the key
     * @return the balance
     */
    double getBalance(String key);

    /**
     * Sets the balance of the bank.
     * <p>
     *
     * @param key    the key
     * @param amount the amount
     */
    void setBalance(String key, double amount);

}
