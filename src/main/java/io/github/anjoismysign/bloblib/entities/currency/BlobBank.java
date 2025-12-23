package io.github.anjoismysign.bloblib.entities.currency;

import org.jetbrains.annotations.NotNull;

public record BlobBank(@NotNull Wallet getWallet) implements BankAccount {

    /**
     * Deposits the amount into the bank.
     * <p>
     *
     * @param currency the currency
     * @param amount   the amount
     */
    public void deposit(Currency currency, double amount) {
        deposit(currency.getKey(), amount);
    }

    public void deposit(String key, double amount) {
        getWallet().add(key, amount);
    }

    /**
     * Withdraws the amount from the bank.
     * <p>
     *
     * @param currency the currency
     * @param amount   the amount
     */
    void withdraw(Currency currency, double amount) {
        withdraw(currency.getKey(), amount);
    }

    public void withdraw(String key, double amount) {
        getWallet().subtract(key, amount);
    }

    /**
     * Checks if the bank has the amount.
     * <p>
     *
     * @param currency the currency
     * @param amount   the amount
     * @return true if the bank has the amount
     */
    boolean has(Currency currency, double amount) {
        return has(currency.getKey(), amount);
    }

    public boolean has(String key, double amount) {
        return getWallet().has(key, amount);
    }

    /**
     * Gets the balance of the bank.
     * <p>
     *
     * @param currency the currency
     * @return the balance
     */
    double getBalance(Currency currency) {
        return getBalance(currency.getKey());
    }

    public double getBalance(String key) {
        return getWallet().balance(key);
    }

    /**
     * Sets the balance of the bank.
     * <p>
     *
     * @param currency the currency
     * @param amount   the amount
     */
    void setBalance(Currency currency, double amount) {
        setBalance(currency.getKey(), amount);
    }

    public void setBalance(String key, double amount) {
        getWallet().put(key, amount);
    }

    /**
     * Resets the bank to the initial balance.
     * <p>
     *
     * @param currency the currency
     */
    void reset(Currency currency) {
        setBalance(currency.getKey(), currency.getInitialBalance());
    }
}
