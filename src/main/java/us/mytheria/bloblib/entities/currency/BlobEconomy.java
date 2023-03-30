package us.mytheria.bloblib.entities.currency;

import net.milkbowl.vault.economy.IdentityEconomy;
import net.milkbowl.vault.economy.MultiEconomy;
import net.milkbowl.vault.economy.wrappers.MultiEconomyWrapper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import us.mytheria.bloblib.managers.BlobPlugin;

import java.util.Collection;

public class BlobEconomy<T extends WalletOwner> implements MultiEconomy {
    private final WalletOwnerManager<T> manager;
    private final BlobPlugin plugin;

    protected BlobEconomy(WalletOwnerManager<T> manager, boolean force) {
        this.manager = manager;
        this.plugin = manager.getPlugin();
        Bukkit.getScheduler().runTask(plugin, () -> {
            Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");
            if (vault != null) {
                MultiEconomyWrapper wrapper = new MultiEconomyWrapper(this);
                wrapper.registerProviders(force);
            }
        });
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return plugin.getName() + "Economy";
    }

    @Override
    public boolean existsImplementation(String name) {
        return manager.currencyDirector.getObjectManager().searchObject(name).isValid();
    }

    @Override
    public boolean existsImplementation(String name, String worldName) {
        return existsImplementation(name);
    }

    @Override
    public IdentityEconomy getImplementation(String name) {
        return manager.convertOrNull(name);
    }

    @Override
    public IdentityEconomy getDefault() {
        return manager.convertOrNull(manager.getDefaultCurrency().getKey());
    }

    @Override
    public IdentityEconomy getDefault(String world) {
        return getDefault();
    }

    @Override
    public Collection<IdentityEconomy> getAllImplementations() {
        return manager.listAllAsEconomies();
    }

    @Override
    public Collection<IdentityEconomy> getAllImplementations(String world) {
        return getAllImplementations();
    }
}
