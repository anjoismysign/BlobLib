package io.github.anjoismysign.bloblib.vault.permissions;

import org.bukkit.entity.Player;

public interface VaultPermissionsWorker {

    /**
     * Adds a permission to a player.
     *
     * @param player     Player to add permission to
     * @param permission Permission to add
     * @return True if the permission was added
     */

    boolean addPermission(Player player, String permission);

    /**
     * Removes a permission from a player.
     *
     * @param player     Player to remove permission from
     * @param permission Permission to remove
     * @return True if the permission was removed
     */
    boolean removePermission(Player player, String permission);

    /**
     * Adds a permission to a player in a world.
     *
     * @param player     Player to add permission to
     * @param permission Permission to add
     * @param world      World to add permission to
     * @return True if the permission was added
     */
    boolean addPermission(Player player, String permission, String world);

    /**
     * Removes a permission from a player in a world.
     *
     * @param player     Player to remove permission from
     * @param permission Permission to remove
     * @param world      World to remove permission from
     * @return True if the permission was removed
     */
    boolean removePermission(Player player, String permission, String world);


    /**
     * Adds a global(all worlds) permission to a player.
     *
     * @param player     Player to add permission to
     * @param permission Permission to add
     * @return True if the permission was added
     */
    default boolean addGlobalPermission(Player player, String permission) {
        return addPermission(player, permission, null);
    }

    /**
     * Removes a global(all worlds) permission from a player.
     *
     * @param player     Player to remove permission from
     * @param permission Permission to remove
     * @return True if the permission was removed
     */
    default boolean removeGlobalPermission(Player player, String permission) {
        return removePermission(player, permission, null);
    }
}