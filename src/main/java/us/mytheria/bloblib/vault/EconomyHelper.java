package us.mytheria.bloblib.vault;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

public class EconomyHelper {

    /**
     * Checks if the economy provider is available.
     * If using MultiEconomy, it will return the default economy provider.
     *
     * @return The economy provider, or null if not available.
     */
    @Nullable
    public static Economy hasEconomyProvider() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return null;
        }
        RegisteredServiceProvider<Economy> economyServiceProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyServiceProvider != null)
            return economyServiceProvider.getProvider();
        RegisteredServiceProvider<MultiEconomy> multiEconomyServiceProvider =
                Bukkit.getServer().getServicesManager().getRegistration(MultiEconomy.class);
        if (multiEconomyServiceProvider == null)
            return null;
        return multiEconomyServiceProvider.getProvider().getDefault();
    }

    /**
     * @return True if the there's no economy provider registered and no multi-economy provider registered.
     */
    public static boolean canRegisterEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> economyServiceProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyServiceProvider != null)
            return false;
        RegisteredServiceProvider<MultiEconomy> multiEconomyServiceProvider =
                Bukkit.getServer().getServicesManager().getRegistration(MultiEconomy.class);
        return multiEconomyServiceProvider == null;
    }
}
