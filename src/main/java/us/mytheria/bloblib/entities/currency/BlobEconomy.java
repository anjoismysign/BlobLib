package us.mytheria.bloblib.entities.currency;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import us.mytheria.bloblib.managers.BlobPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlobEconomy<T extends WalletOwner> implements Economy {
    private final WalletOwnerManager<T> manager;
    private final BlobPlugin plugin;

    protected BlobEconomy(WalletOwnerManager<T> manager) {
        this.manager = manager;
        this.plugin = manager.getPlugin();
        Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");
        if (vault != null)
            Bukkit.getServicesManager().register(Economy.class, this, vault, ServicePriority.Normal);
    }

    private void updateAsynchronously(T walletOwner) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                manager.sqlCrudManager.update(walletOwner.serializeAllAttributes()));
    }

    public boolean hasRecord(UUID uuid) {
        if (manager.owners.containsKey(uuid))
            return true;
        return manager.sqlCrudManager.exists(uuid.toString());
    }

    public double getBalance(UUID uuid) {
        if (manager.owners.containsKey(uuid)) {
            T walletOwner = manager.owners.get(uuid);
            return walletOwner.getBalance(manager.getDefaultCurrency());
        }
        if (!hasRecord(uuid))
            return 0.0;
        T walletOwner = manager.read(uuid).join();
        return walletOwner.getBalance(manager.getDefaultCurrency());
    }

    public boolean has(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }

    public EconomyResponse withdraw(UUID uuid, double amount) {
        if (manager.owners.containsKey(uuid)) {
            T walletOwner = manager.owners.get(uuid);
            walletOwner.withdraw(manager.getDefaultCurrency(), amount);
            return new EconomyResponse(amount, walletOwner.getBalance(manager.getDefaultCurrency()), EconomyResponse.ResponseType.SUCCESS, null);
        }
        if (!hasRecord(uuid))
            return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "No record found.");
        T walletOwner = manager.read(uuid).join();
        walletOwner.withdraw(manager.getDefaultCurrency(), amount);
        updateAsynchronously(walletOwner);
        return new EconomyResponse(amount, walletOwner.getBalance(manager.getDefaultCurrency()), EconomyResponse.ResponseType.SUCCESS, null);
    }

    public EconomyResponse deposit(UUID uuid, double amount) {
        if (manager.owners.containsKey(uuid)) {
            T walletOwner = manager.owners.get(uuid);
            walletOwner.deposit(manager.getDefaultCurrency(), amount);
            return new EconomyResponse(amount, walletOwner.getBalance(manager.getDefaultCurrency()), EconomyResponse.ResponseType.SUCCESS, null);
        }
        if (!hasRecord(uuid))
            return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "No record found.");
        T walletOwner = manager.read(uuid).join();
        walletOwner.deposit(manager.getDefaultCurrency(), amount);
        updateAsynchronously(walletOwner);
        return new EconomyResponse(amount, walletOwner.getBalance(manager.getDefaultCurrency()), EconomyResponse.ResponseType.SUCCESS, null);
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
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return manager.getDefaultCurrency().getDecimalFormat().getMaximumFractionDigits();
    }

    @Override
    public String format(double amount) {
        return manager.getDefaultCurrency().display(amount);
    }

    @Override
    public String currencyNamePlural() {
        return "";
    }

    @Override
    public String currencyNameSingular() {
        return "";
    }

    @Override
    public boolean hasAccount(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null)
            return false;
        return hasRecord(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return hasRecord(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null)
            return 0;
        return getBalance(player.getUniqueId());
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return getBalance(player.getUniqueId());
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null)
            return false;
        return has(player.getUniqueId(), amount);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return has(player.getUniqueId(), amount);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null)
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found");
        return withdraw(player.getUniqueId(), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return withdraw(player.getUniqueId(), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null)
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found");
        return deposit(player.getUniqueId(), amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return deposit(player.getUniqueId(), amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public List<String> getBanks() {
        plugin.getAnjoLogger().error("A plugin is trying to use Vault's getBanks() method, which is not implemented.");
        return new ArrayList<>();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        plugin.getAnjoLogger().error("A plugin is trying to use Vault's createPlayerAccount() method, which is not needed.");
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        plugin.getAnjoLogger().error("A plugin is trying to use Vault's createPlayerAccount() method, which is not needed.");
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }
}
