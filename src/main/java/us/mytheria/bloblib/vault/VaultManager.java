package us.mytheria.bloblib.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.logger.BlobPluginLogger;
import us.mytheria.bloblib.vault.economy.AbsentEco;
import us.mytheria.bloblib.vault.economy.FoundEco;
import us.mytheria.bloblib.vault.economy.VaultEconomyWorker;
import us.mytheria.bloblib.vault.permissions.AbsentPerms;
import us.mytheria.bloblib.vault.permissions.FoundPerms;
import us.mytheria.bloblib.vault.permissions.VaultPermissionsWorker;

public class VaultManager implements Listener {
    private final BlobPluginLogger logger;
    private VaultEconomyWorker vaultEconomyWorker;
    private VaultPermissionsWorker vaultPermissionsWorker;
    private Economy economy = null;
    private Permission permission = null;
    private boolean vaultEcoInstalled = false;
    private boolean vaultPermsInstalled = false;

    public VaultManager() {
        logger = BlobLib.getAnjoLogger();
        setupEconomy();
        setupPermissions();
        Bukkit.getPluginManager().registerEvents(this, BlobLib.getInstance());
    }

    @EventHandler
    public void onUnregister(ServiceUnregisterEvent e) {
        RegisteredServiceProvider<?> provider = e.getProvider();
        if (provider.getService() == Economy.class) {
            vaultEconomyWorker = new AbsentEco();
        } else if (provider.getService() == Permission.class) {
            vaultPermissionsWorker = new AbsentPerms();
        }
    }

    @EventHandler
    public void onRegister(ServiceRegisterEvent e) {
        RegisteredServiceProvider<?> provider = e.getProvider();
        if (provider.getService() == Economy.class) {
            setupEconomy();
        } else if (provider.getService() == Permission.class) {
            setupPermissions();
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

    private boolean hasEconomyProvider() {
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

    private boolean hasPermissionsProvider() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        permission = rsp.getProvider();
        return permission != null;
    }

    public void setupEconomy() {
        if (!hasEconomyProvider()) {
            logger.log("Vault dependency / economy plugin not found, disabling economy features.");
            vaultEconomyWorker = new AbsentEco();
        } else {
            vaultEconomyWorker = new FoundEco(economy);
            vaultEcoInstalled = true;
        }
    }

    public void setupPermissions() {
        if (!hasPermissionsProvider()) {
            logger.log("Vault dependency / permissions plugin not found, disabling permissions features.");
            vaultPermissionsWorker = new AbsentPerms();
        } else {
            vaultPermissionsWorker = new FoundPerms(permission);
            vaultPermsInstalled = true;
        }
    }
}
