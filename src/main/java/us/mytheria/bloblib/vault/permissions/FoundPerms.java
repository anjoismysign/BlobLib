package us.mytheria.bloblib.vault.permissions;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;

public class FoundPerms implements VaultPermissions {
    private Permission permission;

    public FoundPerms(Permission permission) {
        this.permission = permission;
    }

    /**
     * Add permission to a player ONLY for the world the player is currently on.
     * This is a world-specific operation, if you want to add global permission
     * you must explicitly use NULL for the world.
     *
     * @return true if the permission was added, false if the player already had the permission
     */
    @Override
    public boolean addPermission(Player player, String permission) {
        return this.permission.playerAdd(player, permission);
    }

    /**
     * Remove permission from a player.
     * Will attempt to remove permission from the player on the player's
     * current world. This is NOT a global operation.
     *
     * @return true if the permission was removed, false if the player did not have the permission
     */
    @Override
    public boolean removePermission(Player player, String permission) {
        return this.permission.playerRemove(player, permission);
    }

    /**
     * Add permission to a player. Supports NULL value for World if the permission
     * system registered supports global permissions. But May return odd values if
     * the servers registered permission system does not have a global permission store.
     *
     * @return true if the permission was added, false if the player already had the permission
     */
    @Override
    public boolean addPermission(Player player, String permission, String world) {
        return this.permission.playerAdd(world, player, permission);
    }

    /**
     * Remove permission from a player. Supports NULL value for World if the
     * permission system registered supports global permissions. But May return
     * odd values if the servers registered permission system does not have a
     * global permission store.
     *
     * @return true if the permission was removed, false if the player did not have the permission
     */
    @Override
    public boolean removePermission(Player player, String permission, String world) {
        return this.permission.playerRemove(world, player, permission);
    }
}
