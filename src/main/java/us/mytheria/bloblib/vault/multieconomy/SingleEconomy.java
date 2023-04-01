package us.mytheria.bloblib.vault.multieconomy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.IdentityEconomy;
import net.milkbowl.vault.economy.MultiEconomy;
import net.milkbowl.vault.economy.wrappers.EconomyWrapper;

import java.util.Collection;
import java.util.List;

/**
 * Allows using MultiEconomy class even if only Economy/IdentityEconomy is provided
 * I would ship this inside VaultAPI2, but I am not sure if most developers would use it...
 */
public class SingleEconomy implements MultiEconomy {
    private final IdentityEconomy economy;

    public static SingleEconomy fromLegacy(Economy economy) {
        EconomyWrapper wrapper = new EconomyWrapper(economy);
        return new SingleEconomy(wrapper.legacy());
    }

    public SingleEconomy(IdentityEconomy economy) {
        this.economy = economy;
    }

    @Override
    public boolean isEnabled() {
        return economy.isEnabled();
    }

    @Override
    public String getName() {
        return economy.getName();
    }

    @Override
    public boolean existsImplementation(String name) {
        return true;
    }

    @Override
    public boolean existsImplementation(String name, String world) {
        return true;
    }

    @Override
    public IdentityEconomy getImplementation(String name) {
        return economy;
    }

    @Override
    public IdentityEconomy getDefault() {
        return economy;
    }

    @Override
    public IdentityEconomy getDefault(String world) {
        return economy;
    }

    @Override
    public Collection<IdentityEconomy> getAllImplementations() {
        return List.of(economy);
    }

    @Override
    public Collection<IdentityEconomy> getAllImplementations(String world) {
        return getAllImplementations();
    }
}
