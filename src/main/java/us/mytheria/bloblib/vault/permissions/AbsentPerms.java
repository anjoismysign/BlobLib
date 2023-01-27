package us.mytheria.bloblib.vault.permissions;

import org.bukkit.entity.Player;

/**
 * A class that does nothing since there is no
 * permissions plugin compatible with Vault.
 */
public class AbsentPerms implements VaultPermissionsWorker {
    @Override
    public boolean addPermission(Player player, String permission) {
        return true;
    }

    /**
     * Does nothing since there is no permissions plugin.
     *
     * @param player     Player to remove permission from
     * @param permission Permission to remove
     * @return true
     */
    @Override
    public boolean removePermission(Player player, String permission) {
        return true;
    }

    /**
     * Does nothing since there is no permissions plugin.
     *
     * @param player     Player to add permission to
     * @param permission Permission to add
     * @param world      World to add permission to
     * @return true
     */
    @Override
    public boolean addPermission(Player player, String permission, String world) {
        return true;
    }

    /**
     * Does nothing since there is no permissions plugin.
     *
     * @param player     Player to remove permission from
     * @param permission Permission to remove
     * @param world      World to remove permission from
     * @return true
     */
    @Override
    public boolean removePermission(Player player, String permission, String world) {
        return true;
    }
}
