package us.mytheria.bloblib;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class BlobLibAPI {
    private static BlobLib main = BlobLib.getInstance();

    /**
     * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param player to deposit to
     * @param amount amount to deposit
     */
    public static void addCash(Player player, double amount) {
        main.getVaultManager().addCash(player, amount);
    }

    /**
     * Withdraws an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param player to deposit to
     * @param amount amount to deposit
     */
    public static void withdrawCash(Player player, double amount) {
        main.getVaultManager().withdrawCash(player, amount);
    }

    /**
     * Accepts negative values.
     * Be sure that vault implementation supports negative values.
     *
     * @param player to deposit to
     * @param amount amount to deposit
     */
    public static void setCash(Player player, double amount) {
        main.getVaultManager().setCash(player, amount);
    }

    /**
     * Checks if a player has a certain amount of money
     *
     * @param player to check
     * @param amount amount to check
     * @return true if player has amount
     */
    public static boolean hasCashAmount(Player player, double amount) {
        return main.getVaultManager().hasCashAmount(player, amount);
    }

    /**
     * Gets the amount of money a player has
     *
     * @param player to check
     * @return amount of money
     */
    public static double getCash(Player player) {
        return main.getVaultManager().getCash(player);
    }

    /**
     * @return true if vault is enabled
     */
    public static boolean isVaultInstalled() {
        return main.getVaultManager().isVaultInstalled();
    }

    /**
     * Creates a hologram
     *
     * @param name     name of hologram
     * @param location Bukkit's Location of hologram
     * @param lines    lines of hologram
     */
    public static void createHologram(String name, Location location, List<String> lines) {
        main.getHologramManager().create(name, location, lines);
    }

    /**
     * Creates a hologram
     *
     * @param name         name of hologram
     * @param location     Bukkit's Location of hologram
     * @param lines        lines of hologram
     * @param saveToConfig if true, hologram will be saved in configuration
     */
    public static void createHologram(String name, Location location, List<String> lines, boolean saveToConfig) {
        main.getHologramManager().create(name, location, lines, saveToConfig);
    }

    /**
     * Updates a hologram
     *
     * @param name name of hologram
     */
    public static void updateHologram(String name) {
        main.getHologramManager().update(name);
    }

    /**
     * Deletes a hologram
     *
     * @param name name of hologram
     */
    public static void removeHologram(String name) {
        main.getHologramManager().remove(name);
    }

    /**
     * Sets a hologram's lines
     *
     * @param name  name of hologram
     * @param lines lines of hologram
     */
    public static void setHologramLines(String name, List<String> lines) {
        main.getHologramManager().setLines(name, lines);
    }
}
