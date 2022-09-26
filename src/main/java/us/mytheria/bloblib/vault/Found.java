package us.mytheria.bloblib.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Found implements Vault {
    private Economy economy;
    private Permission permission;

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        permission = rsp.getProvider();
        return permission != null;
    }

    public Found() {
        setupEconomy();
        setupPermissions();
    }

    @Override
    public void addCash(Player player, double amount) {
        economy.depositPlayer(player, amount);
    }

    @Override
    public void withdrawCash(Player player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

    @Override
    public void setCash(Player player, double amount) {
        economy.withdrawPlayer(player, economy.getBalance(player));
        if (amount > 0.01) {
            economy.depositPlayer(player, amount);
            return;
        }
        if (amount < -0.01) {
            amount = -amount;
            economy.withdrawPlayer(player, amount);
        }
    }

    @Override
    public boolean hasCashAmount(Player player, double amount) {
        return economy.has(player, amount);
    }

    @Override
    public double getCash(Player player) {
        return economy.getBalance(player);
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
