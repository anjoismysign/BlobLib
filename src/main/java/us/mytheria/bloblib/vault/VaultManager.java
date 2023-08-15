package us.mytheria.bloblib.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.MultiEconomy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.logger.BlobPluginLogger;
import us.mytheria.bloblib.vault.economy.Absent;
import us.mytheria.bloblib.vault.economy.Found;
import us.mytheria.bloblib.vault.economy.VaultEconomyWorker;
import us.mytheria.bloblib.vault.multieconomy.ElasticEconomy;
import us.mytheria.bloblib.vault.permissions.AbsentPerms;
import us.mytheria.bloblib.vault.permissions.FoundPerms;
import us.mytheria.bloblib.vault.permissions.VaultPermissionsWorker;

public class VaultManager implements Listener {
    private final BlobPluginLogger logger;
    private VaultEconomyWorker vaultEconomyWorker = new Absent();
    private VaultPermissionsWorker vaultPermissionsWorker;
    private Economy economy = null;
    private ElasticEconomy elasticEconomy = new us.mytheria.bloblib.vault.multieconomy.Absent();
    private Permission permission = null;
    private boolean vaultEcoInstalled = false;
    private boolean vaultMultiEcoInstalled = false;
    private boolean vaultPermsInstalled = false;
    private boolean singleEconomy = false;
    private final ServicesManager servicesManager = Bukkit.getServicesManager();

    public VaultManager() {
        logger = BlobLib.getAnjoLogger();
        setupEconomy();
        setupPermissions();
        Bukkit.getPluginManager().registerEvents(this, BlobLib.getInstance());
    }

    @EventHandler
    public void onUnregister(ServiceUnregisterEvent event) {
        RegisteredServiceProvider<?> eventProvider = event.getProvider();
        switch (eventProvider.getService().getName()) {
            case "net.milkbowl.vault.economy.Economy" -> {
                vaultEconomyWorker = new Absent();
                RegisteredServiceProvider<Economy> provider = servicesManager.getRegistration(Economy.class);
                if (provider == null)
                    vaultEcoInstalled = false;
                else
                    setupEconomy();
            }
            case "net.milkbowl.vault.permission.Permission" -> {
                vaultPermissionsWorker = new AbsentPerms();
                RegisteredServiceProvider<Permission> provider = servicesManager.getRegistration(Permission.class);
                if (provider == null)
                    vaultPermsInstalled = false;
                else
                    setupPermissions();
            }
            case "net.milkbowl.vault.economy.MultiEconomy" -> {
                elasticEconomy = new us.mytheria.bloblib.vault.multieconomy.Absent();
                RegisteredServiceProvider<MultiEconomy> provider = servicesManager.getRegistration(MultiEconomy.class);
                if (provider == null) {
                    vaultMultiEcoInstalled = false;
                    singleEconomy = false;
                } else
                    setupEconomy();
            }
            default -> {
            }
        }
    }

    @SuppressWarnings("unchecked")
    @EventHandler
    public void onRegister(ServiceRegisterEvent event) {
        RegisteredServiceProvider<?> eventProvider = event.getProvider();
        switch (eventProvider.getService().getName()) {
            case "net.milkbowl.vault.economy.Economy" -> {
                RegisteredServiceProvider<Economy> provider = (RegisteredServiceProvider<Economy>) eventProvider;
                vaultEconomyWorker = new Found(provider.getProvider());
                vaultEcoInstalled = true;
                Bukkit.getLogger().fine("Vault Economy override");
            }
            case "net.milkbowl.vault.permission.Permission" -> {
                RegisteredServiceProvider<Permission> provider = (RegisteredServiceProvider<Permission>) eventProvider;
                vaultPermissionsWorker = new FoundPerms(provider.getProvider());
                vaultPermsInstalled = true;
                Bukkit.getLogger().fine("Vault Permissions override");
            }
            case "net.milkbowl.vault.economy.MultiEconomy" -> {
                RegisteredServiceProvider<MultiEconomy> provider = (RegisteredServiceProvider<MultiEconomy>) eventProvider;
                elasticEconomy = ElasticEconomy.of(provider.getProvider());
                vaultMultiEcoInstalled = true;
                Bukkit.getLogger().fine("Vault MultiEconomy override");
            }
        }

        if (eventProvider.getService() == Economy.class ||
                eventProvider.getService() == MultiEconomy.class) {
            setupEconomy();
        } else if (eventProvider.getService() == Permission.class) {
            setupPermissions();
        }
    }

    /**
     * @return Vault Economy Worker
     * @Deprecated Use {@link #getElasticEconomy()} instead
     * If no MultiEconomy is provided by the Economy Plugin,
     * all implementations will return the same Economy,
     * which is also the default economy.
     */
    @Deprecated
    public VaultEconomyWorker getVaultEconomyWorker() {
        return vaultEconomyWorker;
    }

    public VaultPermissionsWorker getVaultPermissionsWorker() {
        return vaultPermissionsWorker;
    }

    public ElasticEconomy getElasticEconomy() {
        return elasticEconomy;
    }

    public boolean isVaultEcoInstalled() {
        return vaultEcoInstalled;
    }

    public boolean isVaultPermsInstalled() {
        return vaultPermsInstalled;
    }

    public boolean isVaultMultiEcoInstalled() {
        return vaultMultiEcoInstalled;
    }

    public boolean isSingleEconomy() {
        return singleEconomy;
    }

    /**
     * Determines if all packages in a String array are within the Classpath
     * This is the best way to determine if a specific plugin exists and will be
     * loaded. If the plugin package isn't loaded, we shouldn't bother waiting
     * for it!
     *
     * @param packages String Array of package names to check
     * @return Success or Failure
     */
    private static boolean packagesExists(String... packages) {
        try {
            for (String pkg : packages) {
                Class.forName(pkg);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean hasEconomyProvider() {
        RegisteredServiceProvider<Economy> economyServiceProvider = servicesManager.getRegistration(Economy.class);
        if (economyServiceProvider == null)
            return false;
        vaultEcoInstalled = true;
        economy = economyServiceProvider.getProvider();
        vaultEconomyWorker = new Found(economy);
        return true;
    }

    private boolean hasMultiEconomyProvider() {
        if (!packagesExists("net.milkbowl.vault.economy.MultiEconomy")) {
            Bukkit.getLogger().info("Not using Vault2, disabling MultiEconomy features");
            return false;
        }
        Bukkit.getLogger().info("Detected Vault2, enabling MultiEconomy features");
        RegisteredServiceProvider<MultiEconomy> multieconomyServiceProvider = servicesManager.getRegistration(MultiEconomy.class);
        if (multieconomyServiceProvider == null) {
            if (!vaultEcoInstalled)
                return false;
            RegisteredServiceProvider<Economy> economyServiceProvider = servicesManager.getRegistration(Economy.class);
            ElasticEconomy legacy = ElasticEconomy.of(economyServiceProvider.getProvider());
            if (legacy == null)
                return false;
            elasticEconomy = legacy;
            vaultMultiEcoInstalled = true;
            singleEconomy = true;
            return true;
        }
        ElasticEconomy multi = ElasticEconomy.of(multieconomyServiceProvider.getProvider());
        if (multi == null)
            return false;
        vaultMultiEcoInstalled = true;
        elasticEconomy = multi;
        return true;
    }

    private boolean hasPermissionsProvider() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null)
            return false;
        permission = rsp.getProvider();
        return permission != null;
    }

    public void setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null)
            return;
        hasEconomyProvider();
        hasMultiEconomyProvider();
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
