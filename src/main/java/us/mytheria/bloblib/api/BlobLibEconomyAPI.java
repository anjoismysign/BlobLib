package us.mytheria.bloblib.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.vault.multieconomy.ElasticEconomy;

public class BlobLibEconomyAPI {
    private static BlobLibEconomyAPI instance;
    private final BlobLib plugin;

    private BlobLibEconomyAPI(BlobLib plugin) {
        this.plugin = plugin;
    }

    public static BlobLibEconomyAPI getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibEconomyAPI.instance = new BlobLibEconomyAPI(plugin);
        }
        return instance;
    }

    public static BlobLibEconomyAPI getInstance() {
        return getInstance(null);
    }

    /**
     * @return The ElasticEconomy
     */
    public ElasticEconomy getElasticEconomy() {
        ElasticEconomy economy = plugin.getVaultManager().getElasticEconomy();
        if (economy.isAbsent())
            Bukkit.getLogger().severe("ElasticEconomy is not present. This is " +
                    "because there is no legacy Economy provider nor a MultiEconomy provider...");
        return economy;
    }

    /**
     * Formats the given amount of cash.
     *
     * @param amount Amount to format
     * @return Formatted amount
     * @deprecated Use {@link #getElasticEconomy()} instead
     */
    @Deprecated
    public String format(double amount) {
        return plugin.getVaultManager().getVaultEconomyWorker().format(amount);
    }

    /**
     * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param player to deposit to
     * @param amount amount to deposit
     * @deprecated Use {@link #getElasticEconomy()} instead
     */
    @Deprecated
    public void addCash(Player player, double amount) {
        plugin.getVaultManager().getVaultEconomyWorker().addCash(player, amount);
    }

    /**
     * Withdraws an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param player to deposit to
     * @param amount amount to deposit
     * @deprecated Use {@link #getElasticEconomy()} instead
     */
    @Deprecated
    public void withdrawCash(Player player, double amount) {
        plugin.getVaultManager().getVaultEconomyWorker().withdrawCash(player, amount);
    }

    /**
     * Accepts negative values.
     * Be sure that vault implementation supports negative values.
     *
     * @param player to deposit to
     * @param amount amount to deposit
     * @deprecated Use {@link #getElasticEconomy()} instead
     */
    @Deprecated
    public void setCash(Player player, double amount) {
        plugin.getVaultManager().getVaultEconomyWorker().setCash(player, amount);
    }

    /**
     * Checks if a player has a certain amount of money
     *
     * @param player to check
     * @param amount amount to check
     * @return true if player has amount
     * @deprecated Use {@link #getElasticEconomy()} instead
     */
    @Deprecated
    public boolean hasCashAmount(Player player, double amount) {
        return plugin.getVaultManager().getVaultEconomyWorker().hasCashAmount(player, amount);
    }

    /**
     * Gets the amount of money a player has
     *
     * @param player to check
     * @return amount of money
     * @deprecated Use {@link #getElasticEconomy()} instead
     */
    @Deprecated
    public double getCash(Player player) {
        return plugin.getVaultManager().getVaultEconomyWorker().getCash(player);
    }

    /**
     * @return true if a vault economy provider plugin is being used
     * @deprecated Use {@link #getElasticEconomy()} instead
     */
    @Deprecated
    public boolean hasVaultEconomyProvider() {
        return plugin.getVaultManager().isVaultEcoInstalled();
    }

    /**
     * @return true if a vault permission provider plugin is being used
     */
    public boolean hasVaultPermissionsProvider() {
        return plugin.getVaultManager().isVaultPermsInstalled();
    }
}
