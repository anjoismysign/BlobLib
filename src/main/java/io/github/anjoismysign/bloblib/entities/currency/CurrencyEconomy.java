package io.github.anjoismysign.bloblib.entities.currency;

import io.github.anjoismysign.anjo.entities.Uber;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.IdentityEconomy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class CurrencyEconomy implements IdentityEconomy {
    private final Currency currency;
    private final WalletOwnerManager<?> walletOwnerManager;


    protected CurrencyEconomy(Currency currency, WalletOwnerManager<?> walletOwnerManager) {
        this.currency = currency;
        this.walletOwnerManager = walletOwnerManager;
    }

    private Optional<? extends WalletOwner> isOnline(UUID uuid) {
        return walletOwnerManager.isWalletOwner(uuid);
    }

    private boolean ifIsOnline(UUID uuid, Consumer<WalletOwner> consumer) {
        Optional<? extends WalletOwner> walletOwner = isOnline(uuid);
        if (walletOwner.isPresent()) {
            consumer.accept(walletOwner.get());
            return true;
        } else
            return false;
    }

    @Override
    public boolean supportsAllRecordsOperation() {
        return false;
    }

    @Override
    public boolean supportsAllOnlineOperation() {
        return true;
    }

    @Override
    public boolean supportsOfflineOperations() {
        return false;
    }

    @Override
    public boolean supportsUUIDOperations() {
        return false;
    }

    @Override
    public boolean createAccount(UUID uuid, String s) {
        return true;
    }

    @Override
    public boolean createAccount(UUID uuid, String s, String s1) {
        return true;
    }

    @Override
    public Map<UUID, String> getAllRecords() {
        try {
            throw new OperationNotSupportedException("getAllRecords() is not supported by CurrencyEconomy");
        } catch (OperationNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<UUID> getAllOnline() {
        return walletOwnerManager.walletOwners.keySet();
    }

    @Override
    public String getAccountName(UUID uuid) {
        Uber<String> name = Uber.fly();
        boolean isOnline = ifIsOnline(uuid, walletOwner ->
                name.talk(walletOwner.getPlayerName()));
        if (!isOnline)
            throw new IllegalArgumentException("Not online " + uuid);
        return name.thanks();
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        return walletOwnerManager.isWalletOwner(uuid).isPresent();
    }

    @Override
    public boolean hasAccount(UUID uuid, String s) {
        return hasAccount(uuid);
    }

    public boolean renameAccount(UUID uuid, String name) {
        return isOnline(uuid).isPresent();
    }

    @Override
    public double getBalance(UUID uuid) {
        Uber<Double> balance = Uber.fly();
        boolean hasRecord = ifIsOnline(uuid, walletOwner ->
                balance.talk(walletOwner.getBalance(currency.getKey())));
        if (!hasRecord)
            return 0;
        return balance.thanks();
    }

    @Override
    public double getBalance(UUID uuid, String s) {
        return getBalance(uuid);
    }

    @Override
    public boolean has(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }

    @Override
    public boolean has(UUID uuid, String s, double amount) {
        return getBalance(uuid) >= amount;
    }

    @Override
    public EconomyResponse withdraw(UUID uuid, double amount) {
        Uber<Boolean> has = Uber.drive(false);
        ifIsOnline(uuid, walletOwner -> {
            if (!has(uuid, amount)) {
                @Nullable Function<NotEnoughBalance, Boolean> function = walletOwnerManager.getNotEnoughEvent();
                if (function != null){
                    double current = getBalance(uuid);
                    double missing = amount - current;
                    NotEnoughBalance event = new NotEnoughBalance(walletOwner, currency.getKey(), missing);
                    boolean eventResult = function.apply(event);
                    if (!eventResult){
                        has.talk(false);
                        return;
                    }
                    boolean canProceed = getBalance(uuid) >= amount;
                    if (!canProceed){
                        has.talk(false);
                    }
                    walletOwner.withdraw(currency.getKey(), amount);
                    has.talk(true);
                }
                has.talk(false);
                return;
            }
            walletOwner.withdraw(currency.getKey(), amount);
            has.talk(true);
        });
        if (has.thanks()) {
            return new EconomyResponse(amount, getBalance(uuid), EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(amount, getBalance(uuid), EconomyResponse.ResponseType.FAILURE, null);
        }
    }

    @Override
    public EconomyResponse withdraw(UUID uuid, String s, double amount) {
        return withdraw(uuid, amount);
    }

    @Override
    public EconomyResponse deposit(UUID uuid, double amount) {
        Uber<Boolean> has = new Uber<>(false);
        ifIsOnline(uuid, walletOwner -> {
            walletOwner.deposit(currency.getKey(), amount);
            has.talk(true);
        });
        if (has.thanks())
            return new EconomyResponse(amount, getBalance(uuid), EconomyResponse.ResponseType.SUCCESS, null);
        else
            return new EconomyResponse(amount, getBalance(uuid), EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Offline operations not implemented");
    }

    @Override
    public EconomyResponse deposit(UUID uuid, String s, double amount) {
        return deposit(uuid, amount);
    }

    @Override
    public EconomyResponse createBank(String s, UUID uuid) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse isBankOwner(String s, UUID uuid) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse isBankMember(String s, UUID uuid) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return currency.getKey();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return currency.getDecimalFormat().getMinimumFractionDigits();
    }

    @Override
    public String format(double amount) {
        return currency.display(amount);
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
    public boolean hasAccount(String s) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }

    @Override
    public double getBalance(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null)
            return 0;
        return getBalance(player.getUniqueId());
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return getBalance(offlinePlayer.getUniqueId());
    }

    @Override
    public double getBalance(String playerName, String s1) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return getBalance(offlinePlayer);
    }

    @Override
    public boolean has(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null)
            return false;
        return has(player.getUniqueId(), amount);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double amount) {
        return has(offlinePlayer.getUniqueId(), amount);
    }

    @Override
    public boolean has(String playerName, String s1, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double amount) {
        return has(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null)
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "Player not online");
        return withdraw(player.getUniqueId(), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        return withdrawPlayer(offlinePlayer.getName(), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String s1, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double amount) {
        return withdrawPlayer(offlinePlayer.getName(), amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null)
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "Player not online");
        return deposit(player.getUniqueId(), amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        return depositPlayer(offlinePlayer.getName(), amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String s1, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double amount) {
        return depositPlayer(offlinePlayer.getName(), amount);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String s1) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return true;
    }
}
