package us.mytheria.bloblib.vault.multieconomy;

import net.milkbowl.vault.economy.IdentityEconomy;

import java.util.Collection;

/**
 * A class that does nothing since there is no
 * economy plugin compatible with Vault.
 */
public class Absent extends ElasticEconomy {
    public Absent() {
        super(null,
                ElasticEconomyType.ABSENT);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean existsImplementation(String name) {
        return false;
    }

    @Override
    public boolean existsImplementation(String name, String world) {
        return false;
    }

    @Override
    public IdentityEconomy getImplementation(String name) {
        return null;
    }

    @Override
    public IdentityEconomy getDefault() {
        return null;
    }

    @Override
    public IdentityEconomy getDefault(String world) {
        return null;
    }

    @Override
    public Collection<IdentityEconomy> getAllImplementations() {
        return null;
    }

    @Override
    public Collection<IdentityEconomy> getAllImplementations(String world) {
        return null;
    }
}
