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
        main.getVaultManager().getVaultEconomyWorker().addCash(player, amount);
    }

    /**
     * Withdraws an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param player to deposit to
     * @param amount amount to deposit
     */
    public static void withdrawCash(Player player, double amount) {
        main.getVaultManager().getVaultEconomyWorker().withdrawCash(player, amount);
    }

    /**
     * Accepts negative values.
     * Be sure that vault implementation supports negative values.
     *
     * @param player to deposit to
     * @param amount amount to deposit
     */
    public static void setCash(Player player, double amount) {
        main.getVaultManager().getVaultEconomyWorker().setCash(player, amount);
    }

    /**
     * Checks if a player has a certain amount of money
     *
     * @param player to check
     * @param amount amount to check
     * @return true if player has amount
     */
    public static boolean hasCashAmount(Player player, double amount) {
        return main.getVaultManager().getVaultEconomyWorker().hasCashAmount(player, amount);
    }

    /**
     * Gets the amount of money a player has
     *
     * @param player to check
     * @return amount of money
     */
    public static double getCash(Player player) {
        return main.getVaultManager().getVaultEconomyWorker().getCash(player);
    }

    /**
     * @return true if a vault economy plugin is being used
     */
    public static boolean hasVaultEconomyDependency() {
        return main.getVaultManager().isVaultEcoInstalled();
    }

    /**
     * @return true if a vault permission plugin is being used
     */
    public static boolean hasVaultPermissionsDependency() {
        return main.getVaultManager().isVaultPermsInstalled();
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

    /**
     * Add permission to a player ONLY for the world the player is currently on.
     * This is a world-specific operation, if you want to add global permission
     * you must explicitly use NULL for the world.
     *
     * @return true if the permission was added, false if the player already had the permission
     */
    public static void addPermission(Player player, String permission) {
        main.getVaultManager().getVaultPermissionsWorker().addPermission(player, permission);
    }

    /**
     * Remove permission from a player.
     * Will attempt to remove permission from the player on the player's
     * current world. This is NOT a global operation.
     *
     * @return true if the permission was removed, false if the player did not have the permission
     */
    public static void removePermission(Player player, String permission) {
        main.getVaultManager().getVaultPermissionsWorker().removePermission(player, permission);
    }

    /**
     * Add permission to a player. Supports NULL value for World if the permission
     * system registered supports global permissions. But May return odd values if
     * the servers registered permission system does not have a global permission store.
     *
     * @return true if the permission was added, false if the player already had the permission
     */
    public static void addPermission(Player player, String permission, String world) {
        main.getVaultManager().getVaultPermissionsWorker().addPermission(player, permission, world);
    }

    /**
     * Remove permission from a player. Supports NULL value for World if the
     * permission system registered supports global permissions. But May return
     * odd values if the servers registered permission system does not have a
     * global permission store.
     *
     * @return true if the permission was removed, false if the player did not have the permission
     */
    public static void removePermission(Player player, String permission, String world) {
        main.getVaultManager().getVaultPermissionsWorker().removePermission(player, permission, world);
    }
}
