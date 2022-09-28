package us.mytheria.bloblib.vault.permissions;

import org.bukkit.entity.Player;

public class AbsentPerms implements VaultPermissions {
    @Override
    public boolean addPermission(Player player, String permission) {
        return true;
    }

    @Override
    public boolean removePermission(Player player, String permission) {
        return true;
    }

    @Override
    public boolean addPermission(Player player, String permission, String world) {
        return true;
    }

    @Override
    public boolean removePermission(Player player, String permission, String world) {
        return true;
    }
}
