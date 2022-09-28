package us.mytheria.bloblib.vault.permissions;

import org.bukkit.entity.Player;

public interface VaultPermissions {

    boolean addPermission(Player player, String permission);

    boolean removePermission(Player player, String permission);

    boolean addPermission(Player player, String permission, String world);

    boolean removePermission(Player player, String permission, String world);
}
