package us.mytheria.bloblib.api;

import org.bukkit.Bukkit;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.vault.multieconomy.ElasticEconomy;

public class BlobLibEconomyAPI {
    private static BlobLibEconomyAPI instance;
    private final BlobLib plugin;

    private BlobLibEconomyAPI(BlobLib plugin) {
        this.plugin = plugin;
    }

    public static BlobLibEconomyAPI getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibEconomyAPI.instance = new BlobLibEconomyAPI(plugin);
        }
        return instance;
    }

    public static BlobLibEconomyAPI getInstance() {
        return getInstance(null);
    }

    /**
     * @return The ElasticEconomy
     */
    public ElasticEconomy getElasticEconomy() {
        ElasticEconomy economy = plugin.getVaultManager().getElasticEconomy();
        if (economy.isAbsent())
            Bukkit.getLogger().severe("ElasticEconomy is not present. This is " +
                    "because there is no legacy Economy provider nor a MultiEconomy provider...");
        return economy;
    }

    /**
     * @return true if a vault permission provider plugin is being used
     */
    public boolean hasVaultPermissionsProvider() {
        return plugin.getVaultManager().isVaultPermsInstalled();
    }
}
