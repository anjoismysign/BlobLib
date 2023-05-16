package us.mytheria.bloblib;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.disguises.Disguiser;
import us.mytheria.bloblib.entities.BlobEditor;
import us.mytheria.bloblib.entities.inventory.VariableSelector;
import us.mytheria.bloblib.entities.listeners.*;
import us.mytheria.bloblib.vault.multieconomy.ElasticEconomy;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author anjoismysign
 * This class provides static methods to interact with the BlobLib API.
 * It's not meant to change unless in a future in a big rewrite of the API.
 */
public class BlobLibAPI {
    private static final BlobLib main = BlobLib.getInstance();

    /**
     * Adds a new chat listener
     *
     * @param player            The player
     * @param timeout           The timeout
     * @param consumer          The consumer. The String is the message sent by the player
     * @param timeoutMessageKey The timeout message key
     * @param timerMessageKey   The timer message key
     */
    public static void addChatListener(Player player, long timeout, Consumer<String> consumer,
                                       String timeoutMessageKey, String timerMessageKey) {
        BlobLib.getInstance().getChatManager().addChatListener(player,
                BlobChatListener.smart(player, timeout, consumer, timeoutMessageKey, timerMessageKey));
    }

    /**
     * Adds a new drop listener
     *
     * @param player          The player
     * @param consumer        The consumer. The ItemStack is the item dropped.
     * @param timerMessageKey The timer message key
     */
    public static void addDropListener(Player player, Consumer<ItemStack> consumer,
                                       String timerMessageKey) {
        BlobLib.getInstance().getDropListenerManager().addDropListener(player,
                BlobDropListener.smart(player, consumer, timerMessageKey));
    }

    /**
     * Adds a new position listener
     *
     * @param player            The player
     * @param timeout           The timeout
     * @param consumer          The consumer. The Block is the block clicked.
     * @param timeoutMessageKey The timeout message key
     * @param timerMessageKey   The timer message key
     */
    public static void addPositionListener(Player player, long timeout, Consumer<Block> consumer,
                                           String timeoutMessageKey, String timerMessageKey) {
        BlobLib.getInstance().getPositionManager().addPositionListener(player,
                BlobSelPosListener.smart(player, timeout, consumer, timeoutMessageKey, timerMessageKey));
    }

    /**
     * Adds a new selector listener
     *
     * @param player          The player
     * @param consumer        The consumer. The argument is the item selected.
     * @param timerMessageKey The timer message key
     * @param selector        The selector
     * @param <T>             The type of the selector
     */
    public static <T> void addSelectorListener(Player player, Consumer<T> consumer,
                                               @Nullable String timerMessageKey,
                                               VariableSelector<T> selector) {
        BlobLib.getInstance().getSelectorManager().addSelectorListener(player,
                BlobSelectorListener.wise(player, consumer, timerMessageKey, selector));
    }

    public static <T> void addEditorListener(Player player, Consumer<T> consumer,
                                             String timerMessageKey,
                                             BlobEditor<T> editor) {
        BlobLib.getInstance().getSelectorManager().addEditorListener(player,
                BlobEditorListener.wise(player, consumer, timerMessageKey, editor));
    }

    /**
     * @return The ElasticEconomy
     */
    public static ElasticEconomy getElasticEconomy() {
        ElasticEconomy economy = main.getVaultManager().getElasticEconomy();
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
    public static String format(double amount) {
        return main.getVaultManager().getVaultEconomyWorker().format(amount);
    }

    /**
     * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param player to deposit to
     * @param amount amount to deposit
     * @deprecated Use {@link #getElasticEconomy()} instead
     */
    @Deprecated
    public static void addCash(Player player, double amount) {
        main.getVaultManager().getVaultEconomyWorker().addCash(player, amount);
    }

    /**
     * Withdraws an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param player to deposit to
     * @param amount amount to deposit
     * @deprecated Use {@link #getElasticEconomy()} instead
     */
    @Deprecated
    public static void withdrawCash(Player player, double amount) {
        main.getVaultManager().getVaultEconomyWorker().withdrawCash(player, amount);
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
    public static void setCash(Player player, double amount) {
        main.getVaultManager().getVaultEconomyWorker().setCash(player, amount);
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
    public static boolean hasCashAmount(Player player, double amount) {
        return main.getVaultManager().getVaultEconomyWorker().hasCashAmount(player, amount);
    }

    /**
     * Gets the amount of money a player has
     *
     * @param player to check
     * @return amount of money
     * @deprecated Use {@link #getElasticEconomy()} instead
     */
    @Deprecated
    public static double getCash(Player player) {
        return main.getVaultManager().getVaultEconomyWorker().getCash(player);
    }

    /**
     * @return true if a vault economy plugin is being used
     * @deprecated Use {@link #getElasticEconomy()} instead
     */
    @Deprecated
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

    /**
     * Will return the Disguiser implementation
     * WARNING! This method will throw an IllegalStateException if called before the world loads!
     * <p>
     * Disguiser interface was made in order to support/cover
     * a wide range of disguise plugins while at the same time
     * allowing disguising methods to be called without failing
     * fast!
     * <p>
     * For future reference, if you want to use the Disguiser,
     * be sure to not cache it in a variable, as we might see
     * Plugman being updated :D
     *
     * @return Disguiser
     */
    public static Disguiser getDisguiser() {
        return main.getDisguiseManager().getDisguiser();
    }
}
