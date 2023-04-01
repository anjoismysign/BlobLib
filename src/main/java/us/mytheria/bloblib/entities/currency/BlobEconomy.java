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
    private final IdentityEconomy defaultEconomy;

    protected BlobEconomy(WalletOwnerManager<T> manager, boolean force) {
        this.manager = manager;
        this.plugin = manager.getPlugin();
        manager.updateImplementations();
        this.defaultEconomy = manager.getImplementation(manager.getDefaultCurrency().getKey());
        Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");
        if (vault != null) {
            MultiEconomyWrapper wrapper = new MultiEconomyWrapper(this);
            wrapper.registerProviders(force);
        }
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
        return manager.getImplementation(name);
    }

    @Override
    public IdentityEconomy getDefault() {
        return defaultEconomy;
    }

    @Override
    public IdentityEconomy getDefault(String world) {
        return getDefault();
    }

    @Override
    public Collection<IdentityEconomy> getAllImplementations() {
        return manager.retrieveImplementations();
    }

    @Override
    public Collection<IdentityEconomy> getAllImplementations(String world) {
        return getAllImplementations();
    }
}
