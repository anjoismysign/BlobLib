package us.mytheria.bloblib.vault.multieconomy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.IdentityEconomy;
import net.milkbowl.vault.economy.MultiEconomy;

import java.util.Collection;

/**
 * Allows both MultiEconomy and legacy Economy (in an instance of SingleEconomy)
 * to be used without needing to implement different abstractions in each plugin.
 */
public class ElasticEconomy implements MultiEconomy {
    private final MultiEconomy economy;
    private final ElasticEconomyType type;

    public static ElasticEconomy of(Economy economy) {
        return new ElasticEconomy(SingleEconomy.fromLegacy(economy), ElasticEconomyType.SINGLE);
    }

    public static ElasticEconomy of(MultiEconomy economy) {
        return new ElasticEconomy(economy, ElasticEconomyType.MULTI);
    }

    public static ElasticEconomy absent() {
        return new ElasticEconomy(new Absent(), ElasticEconomyType.ABSENT);
    }

    /**
     * Creates a new ElasticEconomy instance.
     *
     * @param economy The economy to use.
     * @param type    The type of economy.
     */
    protected ElasticEconomy(MultiEconomy economy,
                             ElasticEconomyType type) {
        this.economy = economy;
        this.type = type;
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
        return economy.existsImplementation(name);
    }

    @Override
    public boolean existsImplementation(String name, String world) {
        return economy.existsImplementation(name, world);
    }

    @Override
    public IdentityEconomy getImplementation(String name) {
        return economy.getImplementation(name);
    }

    @Override
    public IdentityEconomy getDefault() {
        return economy.getDefault();
    }

    @Override
    public IdentityEconomy getDefault(String world) {
        return economy.getDefault(world);
    }

    @Override
    public Collection<IdentityEconomy> getAllImplementations() {
        return economy.getAllImplementations();
    }

    @Override
    public Collection<IdentityEconomy> getAllImplementations(String world) {
        return economy.getAllImplementations(world);
    }

    /**
     * If true, all implementations, even the default one, will return the same.
     *
     * @return true if instance of SingleEconomy
     */
    public boolean isSingleEconomy() {
        return type == ElasticEconomyType.SINGLE;
    }

    /**
     * If true, there is no provider for legacy Economy and MultiEconomy.
     * This case (returning true), can only be given if there's no economy Vault compatible
     * economy plugin or if the economy plugin developer/author didn't provide to Vault.
     *
     * @return true if there's no Vault economy provider
     */
    public boolean isAbsent() {
        return type == ElasticEconomyType.ABSENT;
    }

    /**
     * If true, it means that the economy plugin/provider does actually handle multi-currency.
     *
     * @return true if economy provider handles multi-currency
     */
    public boolean isMultiEconomy() {
        return type == ElasticEconomyType.MULTI;
    }

    public ElasticEconomyType getType() {
        return type;
    }
}
