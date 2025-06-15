package io.github.anjoismysign.bloblib.vault.economy;

import org.bukkit.entity.Player;

public interface VaultEconomyWorker {

    /**
     * In case of being an economy from MultiEconomy, it is expected to return
     * the currency name.
     * Else it's the name of the economy plugin or the name of the economy implementation.
     *
     * @return Name of the currency
     */
    String getName();

    /**
     * Gives the given amount of cash to the player.
     *
     * @param player Player to give cash to
     * @param amount Amount to give
     */
    void addCash(Player player, double amount);

    /**
     * Withdraws the given amount of cash from the player.
     *
     * @param player Player to withdraw cash from
     * @param amount Amount to withdraw
     */
    void withdrawCash(Player player, double amount);

    /**
     * Sets the amount of cash the player has.
     *
     * @param player Player to set cash for
     * @param amount Amount to set
     */
    void setCash(Player player, double amount);

    /**
     * Checks if the player has the given amount of cash.
     *
     * @param player Player to check
     * @param amount Amount to check
     * @return True if the player has the given amount of cash
     */
    boolean hasCashAmount(Player player, double amount);

    /**
     * Returns the amount of cash the player has.
     *
     * @param player Player to check
     * @return Amount of cash the player has
     */
    double getCash(Player player);

    /**
     * Formats the given amount of cash.
     *
     * @param amount Amount to format
     * @return Formatted amount
     */
    String format(double amount);
}
