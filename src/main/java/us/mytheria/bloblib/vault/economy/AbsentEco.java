package us.mytheria.bloblib.vault.economy;

import org.bukkit.entity.Player;

/**
 * A class that does nothing since there is no
 * economy plugin compatible with Vault.
 */
public class AbsentEco implements VaultEconomyWorker {

    /**
     * Does nothing since there is no economy plugin.
     *
     * @param player Player to give cash to
     * @param amount Amount to give
     */
    @Override
    public void addCash(Player player, double amount) {
    }

    /**
     * Does nothing since there is no economy plugin.
     *
     * @param player Player to withdraw cash from
     * @param amount Amount to withdraw
     */
    @Override
    public void withdrawCash(Player player, double amount) {
    }

    /**
     * Does nothing since there is no economy plugin.
     *
     * @param player Player to set cash for
     * @param amount Amount to set
     */
    @Override
    public void setCash(Player player, double amount) {
    }

    /**
     * Always returns false since there is no economy plugin.
     *
     * @param player Player to check
     * @param amount Amount to check
     * @return false
     */
    @Override
    public boolean hasCashAmount(Player player, double amount) {
        return false;
    }

    /**
     * Does nothing since there is no economy plugin.
     *
     * @param player Player to check
     * @return 0
     */
    @Override
    public double getCash(Player player) {
        return 0;
    }
}
