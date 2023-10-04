package us.mytheria.bloblib;

import me.anjoismysign.manobukkit.ManoBukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.api.*;
import us.mytheria.bloblib.disguises.Disguiser;
import us.mytheria.bloblib.entities.BlobEditor;
import us.mytheria.bloblib.entities.inventory.VariableSelector;
import us.mytheria.bloblib.vault.multieconomy.ElasticEconomy;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author anjoismysign
 * This class provides methods to interact with the BlobLib API.
 * It's not meant to change now that it follows the singleton pattern.
 */
public class BlobLibAPI {
    private static BlobLibAPI instance;
    private final BlobLibListenerAPI listenerAPI;
    private final BlobLibEconomyAPI economyAPI;
    private final BlobLibHologramAPI hologramAPI;
    private final BlobLibPermissionAPI permissionAPI;
    private final BlobLibDisguiseAPI disguiseAPI;
    private final BlobLibAssetAPI assetAPI;
    private final ManoBukkit manoBukkit;

    private BlobLibAPI(BlobLib plugin) {
        manoBukkit = ManoBukkit.getInstance();
        this.listenerAPI = BlobLibListenerAPI.getInstance(plugin);
        this.economyAPI = BlobLibEconomyAPI.getInstance(plugin);
        this.hologramAPI = BlobLibHologramAPI.getInstance(plugin);
        this.permissionAPI = BlobLibPermissionAPI.getInstance(plugin);
        this.disguiseAPI = BlobLibDisguiseAPI.getInstance(plugin);
        this.assetAPI = BlobLibAssetAPI.getInstance(plugin);
    }

    public static BlobLibAPI getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibAPI.instance = new BlobLibAPI(plugin);
        }
        return instance;
    }

    public static BlobLibAPI getInstance() {
        return getInstance(null);
    }

    public BlobLibListenerAPI getListenerAPI() {
        return listenerAPI;
    }

    public BlobLibEconomyAPI getEconomyAPI() {
        return economyAPI;
    }

    public BlobLibHologramAPI getHologramAPI() {
        return hologramAPI;
    }

    public BlobLibPermissionAPI getPermissionAPI() {
        return permissionAPI;
    }

    public BlobLibDisguiseAPI getDisguiseAPI() {
        return disguiseAPI;
    }

    public BlobLibAssetAPI getAssetAPI() {
        return assetAPI;
    }

    public ManoBukkit getManoBukkit() {
        return manoBukkit;
    }

    @Deprecated
    public static void addChatListener(Player player, long timeout, Consumer<String> consumer,
                                       String timeoutMessageKey, String timerMessageKey) {
        BlobLibListenerAPI.getInstance().addChatListener(player, timeout, consumer, timeoutMessageKey, timerMessageKey);
    }


    @Deprecated
    public static void addDropListener(Player player, Consumer<ItemStack> consumer,
                                       String timerMessageKey) {
        BlobLibListenerAPI.getInstance().addDropListener(player, consumer, timerMessageKey);
    }

    @Deprecated
    public static void addPositionListener(Player player, long timeout, Consumer<Block> consumer,
                                           String timeoutMessageKey, String timerMessageKey) {
        BlobLibListenerAPI.getInstance().addPositionListener(player, timeout, consumer, timeoutMessageKey, timerMessageKey);
    }

    @Deprecated
    public static <T> void addSelectorListener(Player player, Consumer<T> consumer,
                                               @Nullable String timerMessageKey,
                                               VariableSelector<T> selector) {
        BlobLibListenerAPI.getInstance().addSelectorListener(player, consumer, timerMessageKey, selector);
    }

    @Deprecated
    public static <T> void addEditorListener(Player player, Consumer<T> consumer,
                                             String timerMessageKey,
                                             BlobEditor<T> editor) {
        BlobLibListenerAPI.getInstance().addEditorListener(player, consumer, timerMessageKey, editor);
    }

    @Deprecated
    public static ElasticEconomy getElasticEconomy() {
        return BlobLibEconomyAPI.getInstance().getElasticEconomy();
    }

    @Deprecated
    public static String format(double amount) {
        return BlobLibEconomyAPI.getInstance().format(amount);
    }

    @Deprecated
    public static void addCash(Player player, double amount) {
        BlobLibEconomyAPI.getInstance().addCash(player, amount);
    }

    @Deprecated
    public static void withdrawCash(Player player, double amount) {
        BlobLibEconomyAPI.getInstance().withdrawCash(player, amount);
    }

    @Deprecated
    public static void setCash(Player player, double amount) {
        BlobLibEconomyAPI.getInstance().setCash(player, amount);
    }

    @Deprecated
    public static boolean hasCashAmount(Player player, double amount) {
        return BlobLibEconomyAPI.getInstance().hasCashAmount(player, amount);
    }

    @Deprecated
    public static double getCash(Player player) {
        return BlobLibEconomyAPI.getInstance().getCash(player);
    }

    @Deprecated
    public static boolean hasVaultEconomyDependency() {
        return BlobLibEconomyAPI.getInstance().hasVaultEconomyProvider();
    }

    @Deprecated
    public static boolean hasVaultPermissionsDependency() {
        return BlobLibEconomyAPI.getInstance().hasVaultPermissionsProvider();
    }

    @Deprecated
    public static void createHologram(String name, Location location, List<String> lines) {
        BlobLibHologramAPI.getInstance().createHologram(name, location, lines);
    }

    @Deprecated
    public static void createHologram(String name, Location location, List<String> lines, boolean saveToConfig) {
        BlobLibHologramAPI.getInstance().createHologram(name, location, lines, saveToConfig);
    }

    @Deprecated
    public static void updateHologram(String name) {
        BlobLibHologramAPI.getInstance().updateHologram(name);
    }

    @Deprecated
    public static void removeHologram(String name) {
        BlobLibHologramAPI.getInstance().removeHologram(name);
    }

    @Deprecated
    public static void setHologramLines(String name, List<String> lines) {
        BlobLibHologramAPI.getInstance().setHologramLines(name, lines);
    }

    @Deprecated
    public static boolean addPermission(Player player, String permission) {
        return BlobLibPermissionAPI.getInstance().addPermission(player, permission);
    }

    @Deprecated
    public static boolean removePermission(Player player, String permission) {
        return BlobLibPermissionAPI.getInstance().removePermission(player, permission);
    }

    @Deprecated
    public static boolean addPermission(Player player, String permission, String world) {
        return BlobLibPermissionAPI.getInstance().addPermission(player, permission, world);
    }

    @Deprecated
    public static boolean removePermission(Player player, String permission, String world) {
        return BlobLibPermissionAPI.getInstance().removePermission(player, permission, world);
    }

    @Deprecated
    public static boolean addGlobalPermission(Player player, String permission) {
        return BlobLibPermissionAPI.getInstance().addGlobalPermission(player, permission);
    }

    @Deprecated
    public static boolean removeGlobalPermission(Player player, String permission) {
        return BlobLibPermissionAPI.getInstance().removeGlobalPermission(player, permission);
    }

    @Deprecated
    public static Disguiser getDisguiser() {
        return BlobLibDisguiseAPI.getInstance().getDisguiser();
    }
}
