package us.mytheria.bloblib.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import us.mytheria.bloblib.vault.economy.AbsentEco;
import us.mytheria.bloblib.vault.economy.FoundEco;
import us.mytheria.bloblib.vault.economy.VaultEconomyWorker;
import us.mytheria.bloblib.vault.permissions.AbsentPerms;
import us.mytheria.bloblib.vault.permissions.FoundPerms;
import us.mytheria.bloblib.vault.permissions.VaultPermissionsWorker;

public class VaultManager {
    private VaultEconomyWorker vaultEconomyWorker;
    private VaultPermissionsWorker vaultPermissionsWorker;
    private Economy economy = null;
    private Permission permission = null;
    private boolean vaultEcoInstalled = false;
    private boolean vaultPermsInstalled = false;

    public VaultManager() {
        if (!setupEconomy()) {
            Bukkit.getLogger().info("[BlobLib] Vault dependency / economy plugin not found, disabling economy features.");
            vaultEconomyWorker = new AbsentEco();
        } else {
            vaultEconomyWorker = new FoundEco(economy);
            vaultEcoInstalled = true;
        }
        if (!setupPermissions()) {
            Bukkit.getLogger().info("[BlobLib] Vault dependency / permissions plugin not found, disabling permissions features.");
            vaultPermissionsWorker = new AbsentPerms();
        } else {
            vaultPermissionsWorker = new FoundPerms(permission);
            vaultPermsInstalled = true;
        }
    }

    public VaultEconomyWorker getVaultEconomyWorker() {
        return vaultEconomyWorker;
    }

    public VaultPermissionsWorker getVaultPermissionsWorker() {
        return vaultPermissionsWorker;
    }

    public boolean isVaultEcoInstalled() {
        return vaultEcoInstalled;
    }

    public boolean isVaultPermsInstalled() {
        return vaultPermsInstalled;
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
