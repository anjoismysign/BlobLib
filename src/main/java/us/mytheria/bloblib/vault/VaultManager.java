package us.mytheria.bloblib.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import us.mytheria.bloblib.vault.economy.AbsentEco;
import us.mytheria.bloblib.vault.economy.FoundEco;
import us.mytheria.bloblib.vault.economy.VaultEconomy;
import us.mytheria.bloblib.vault.permissions.AbsentPerms;
import us.mytheria.bloblib.vault.permissions.FoundPerms;
import us.mytheria.bloblib.vault.permissions.VaultPermissions;

public class VaultManager {
    private VaultEconomy vaultEconomy;
    private VaultPermissions vaultPermissions;
    private Economy economy = null;
    private Permission permission = null;
    private boolean vaultEcoInstalled = false;
    private boolean vaultPermsInstalled = false;

    public VaultManager() {
        if (!setupEconomy()) {
            Bukkit.getLogger().info("[BlobLib] Vault dependency / economy plugin not found, disabling economy features.");
            vaultEconomy = new AbsentEco();
        } else {
            vaultEconomy = new FoundEco(economy);
            vaultEcoInstalled = true;
        }
        if (!setupPermissions()) {
            Bukkit.getLogger().info("[BlobLib] Vault dependency / permissions plugin not found, disabling permissions features.");
            vaultPermissions = new AbsentPerms();
        } else {
            vaultPermissions = new FoundPerms(permission);
            vaultPermsInstalled = true;
        }
    }

    public void addCash(Player player, double amount) {
        vaultEconomy.addCash(player, amount);
    }

    public void withdrawCash(Player player, double amount) {
        vaultEconomy.withdrawCash(player, amount);
    }

    public void setCash(Player player, double amount) {
        vaultEconomy.setCash(player, amount);
    }

    public boolean hasCashAmount(Player player, double amount) {
        return vaultEconomy.hasCashAmount(player, amount);
    }

    public double getCash(Player player) {
        return vaultEconomy.getCash(player);
    }

    public boolean isVaultEcoInstalled() {
        return vaultEcoInstalled;
    }

    public boolean isVaultPermsInstalled() {
        return vaultPermsInstalled;
    }

    public boolean addPermission(Player player, String permission) {
        return vaultPermissions.addPermission(player, permission);
    }

    public boolean removePermission(Player player, String permission) {
        return vaultPermissions.removePermission(player, permission);
    }

    public boolean addPermission(Player player, String permission, String world) {
        return vaultPermissions.addPermission(player, permission, world);
    }

    public boolean removePermission(Player player, String permission, String world) {
        return vaultPermissions.removePermission(player, permission, world);
    }

    private boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
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
}
