package us.mytheria.bloblib.api;

import org.bukkit.entity.Player;
import us.mytheria.bloblib.BlobLib;

public class BlobLibPermissionAPI {
    private static BlobLibPermissionAPI instance;
    private final BlobLib plugin;

    private BlobLibPermissionAPI(BlobLib plugin) {
        this.plugin = plugin;
    }

    public static BlobLibPermissionAPI getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibPermissionAPI.instance = new BlobLibPermissionAPI(plugin);
        }
        return instance;
    }

    public static BlobLibPermissionAPI getInstance() {
        return getInstance(null);
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
    public boolean addPermission(Player player, String permission) {
        return plugin.getVaultManager().getVaultPermissionsWorker().addPermission(player, permission);
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
    public boolean removePermission(Player player, String permission) {
        return plugin.getVaultManager().getVaultPermissionsWorker().removePermission(player, permission);
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
    public boolean addPermission(Player player, String permission, String world) {
        return plugin.getVaultManager().getVaultPermissionsWorker().addPermission(player, permission, world);
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
    public boolean removePermission(Player player, String permission, String world) {
        return plugin.getVaultManager().getVaultPermissionsWorker().removePermission(player, permission, world);
    }

    /**
     * Add permission to a player. Will attempt to add permission
     * to the player on the player, GLOBALLY.
     *
     * @param player     The player to add the permission to
     * @param permission The permission to add
     * @return true if the permission was added, false if the player already had the permission
     */
    public boolean addGlobalPermission(Player player, String permission) {
        return plugin.getVaultManager().getVaultPermissionsWorker().addGlobalPermission(player, permission);
    }

    /**
     * Remove permission from a player. Will attempt to remove permission
     * from the player on the player, GLOBALLY.
     *
     * @param player     The player to remove the permission from
     * @param permission The permission to remove
     * @return true if the permission was removed, false if the player did not have the permission
     */
    public boolean removeGlobalPermission(Player player, String permission) {
        return plugin.getVaultManager().getVaultPermissionsWorker().removeGlobalPermission(player, permission);
    }
}
