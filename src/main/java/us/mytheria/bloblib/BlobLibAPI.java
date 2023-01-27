package us.mytheria.bloblib;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.mytheria.bloblib.entities.inventory.BlobInventory;
import us.mytheria.bloblib.entities.message.BlobSound;
import us.mytheria.bloblib.entities.message.ReferenceBlobMessage;
import us.mytheria.bloblib.managers.InventoryManager;
import us.mytheria.bloblib.managers.MessageManager;
import us.mytheria.bloblib.managers.SoundManager;

import java.util.List;

/**
 * @author anjoismysign
 * This class provides static methods to interact with the BlobLib API.
 * It's not meant to change unless in a future in a big rewrite of the API.
 */
public class BlobLibAPI {
    private static final BlobLib main = BlobLib.getInstance();

    /**
     * @return The inventory manager
     */
    public static InventoryManager getInventoryManager() {
        return main.getInventoryManager();
    }

    /**
     * @return The message manager
     */
    public static MessageManager getMessageManager() {
        return main.getMessageManager();
    }

    /**
     * @return The sound manager
     */
    public static SoundManager getSoundManager() {
        return main.getSoundManager();
    }

    /**
     * @param key Key that points to the inventory
     * @return The inventory
     */
    public static BlobInventory getBlobInventory(String key) {
        return getInventoryManager().getInventory(key);
    }

    /**
     * @param key The key of the message
     * @return The message
     */
    public static ReferenceBlobMessage getMessage(String key) {
        return getMessageManager().getMessage(key);
    }

    /**
     * @param key    The key of the message
     * @param player The player to send the message to
     */
    public static void sendMessage(String key, Player player) {
        getMessageManager().send(player, key);
    }

    /**
     * @param key The key of the sound
     * @return The sound
     */
    public static BlobSound getSound(String key) {
        return getSoundManager().getSound(key);
    }

    /**
     * @param key    The key of the sound
     * @param player The player to play the sound
     */
    public static void playSound(String key, Player player) {
        getSoundManager().play(player, key);
    }

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
     * @param player     The player to add the permission to
     * @param permission The permission to add
     * @return true if the permission was added, false if the player already had the permission
     */
    public static boolean addPermission(Player player, String permission) {
        return main.getVaultManager().getVaultPermissionsWorker().addPermission(player, permission);
    }

    /**
     * Remove permission from a player.
     * Will attempt to remove permission from the player on the player's
     * current world. This is NOT a global operation.
     *
     * @param player     The player to remove the permission from
     * @param permission The permission to remove
     * @return true if the permission was removed, false if the player did not have the permission
     */
    public static boolean removePermission(Player player, String permission) {
        return main.getVaultManager().getVaultPermissionsWorker().removePermission(player, permission);
    }

    /**
     * Add permission to a player. Supports NULL value for World if the permission
     * system registered supports global permissions. But May return odd values if
     * the servers registered permission system does not have a global permission store.
     *
     * @param player     The player to add the permission to
     * @param permission The permission to add
     * @param world      The world to add the permission to (null for global)
     * @return true if the permission was added, false if the player already had the permission
     */
    public static boolean addPermission(Player player, String permission, String world) {
        return main.getVaultManager().getVaultPermissionsWorker().addPermission(player, permission, world);
    }

    /**
     * Remove permission from a player. Supports NULL value for World if the
     * permission system registered supports global permissions. But May return
     * odd values if the servers registered permission system does not have a
     * global permission store.
     *
     * @param player     The player to remove the permission from
     * @param permission The permission to remove
     * @param world      The world to remove the permission from
     *                   (null for global)
     * @return true if the permission was removed, false if the player did not have the permission
     */
    public static boolean removePermission(Player player, String permission, String world) {
        return main.getVaultManager().getVaultPermissionsWorker().removePermission(player, permission, world);
    }

    /**
     * Add permission to a player. Will attempt to add permission
     * to the player on the player, GLOBALLY.
     *
     * @param player     The player to add the permission to
     * @param permission The permission to add
     * @return true if the permission was added, false if the player already had the permission
     */
    public static boolean addGlobalPermission(Player player, String permission) {
        return main.getVaultManager().getVaultPermissionsWorker().addGlobalPermission(player, permission);
    }

    /**
     * Remove permission from a player. Will attempt to remove permission
     * from the player on the player, GLOBALLY.
     *
     * @param player     The player to remove the permission from
     * @param permission The permission to remove
     * @return true if the permission was removed, false if the player did not have the permission
     */
    public static boolean removeGlobalPermission(Player player, String permission) {
        return main.getVaultManager().getVaultPermissionsWorker().removeGlobalPermission(player, permission);
    }
}
