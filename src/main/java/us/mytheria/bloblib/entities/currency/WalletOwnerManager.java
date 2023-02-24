package us.mytheria.bloblib.entities.currency;

import me.anjoismysign.anjo.crud.SQLCrudManager;
import me.anjoismysign.anjo.entities.NamingConventions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.BlobCrudable;
import us.mytheria.bloblib.entities.ObjectDirector;
import us.mytheria.bloblib.managers.BlobPlugin;
import us.mytheria.bloblib.managers.Manager;
import us.mytheria.bloblib.managers.ManagerDirector;
import us.mytheria.bloblib.utilities.BlobCrudManagerBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class WalletOwnerManager<T extends WalletOwner> extends Manager implements Listener {
    protected final HashMap<UUID, T> owners;
    private HashSet<UUID> saving;
    protected SQLCrudManager<BlobCrudable> sqlCrudManager;
    private final BlobPlugin plugin;
    private final Function<BlobCrudable, T> walletOwner;
    private final Function<UUID, BlobCrudable> newBorn;
    private final boolean logActivity;
    private final String crudableName;
    private final @Nullable Function<T, Event> joinEvent;
    private final @Nullable Function<T, Event> quitEvent;
    private boolean registeredEconomy;
    private String defaultCurrency;
    protected ObjectDirector<Currency> currencyDirector;

    protected WalletOwnerManager(ManagerDirector managerDirector, Function<UUID, BlobCrudable> newBorn,
                                 Function<BlobCrudable, T> walletOwner,
                                 String crudableName, boolean logActivity,
                                 @Nullable Function<T, Event> joinEvent,
                                 @Nullable Function<T, Event> quitEvent) {
        super(managerDirector);
        plugin = managerDirector.getPlugin();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        owners = new HashMap<>();
        this.newBorn = newBorn;
        this.walletOwner = walletOwner;
        this.logActivity = logActivity;
        this.crudableName = NamingConventions.toPascalCase(crudableName);
        this.joinEvent = joinEvent;
        this.quitEvent = quitEvent;
        this.registeredEconomy = false;
        reload();
    }

    @Override
    public void unload() {
        saveAll();
    }

    @Override
    public void reload() {
        unload();
        saving = new HashSet<>();
        boolean useSQLite = getPlugin().getConfig().getBoolean("Database.UseSQLite");
        if (useSQLite) {
            sqlCrudManager = BlobCrudManagerBuilder.SQLITE_UUID(plugin, "UUID", 36,
                    crudableName, newBorn, logActivity);
        } else {
            sqlCrudManager = BlobCrudManagerBuilder.MYSQL_UUID(plugin, "UUID", 36,
                    crudableName, newBorn, logActivity);
        }
    }

    @EventHandler
    public void onPrejoin(AsyncPlayerPreLoginEvent e) {
        UUID uuid = e.getUniqueId();
        if (saving.contains(uuid))
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You are already saving your data, please try again in a few seconds.");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (sqlCrudManager.exists(uuid.toString())) {
                owners.put(uuid, walletOwner.apply(sqlCrudManager.read(uuid.toString())));
                return;
            }
            T walletOwner = this.walletOwner.apply(sqlCrudManager.createAndRegister(uuid.toString()));
            owners.put(uuid, walletOwner);
            future.complete(walletOwner);
        });
        future.thenAccept(walletOwner -> {
            if (joinEvent != null)
                Bukkit.getPluginManager().callEvent(joinEvent.apply(walletOwner));
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Optional<T> optional = isWalletOwner(uuid);
        if (optional.isEmpty())
            return;
        T walletOwner = optional.get();
        if (quitEvent != null)
            Bukkit.getPluginManager().callEvent(quitEvent.apply(walletOwner));
        saving.add(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            sqlCrudManager.update(walletOwner.serializeAllAttributes());
            saving.remove(uuid);
        });
    }

    public void addObject(T walletOwner) {
        owners.put(walletOwner.getUniqueId(), walletOwner);
    }

    public void removeObject(T lumber) {
        owners.remove(lumber.getUniqueId());
    }

    public Optional<T> isWalletOwner(UUID uuid) {
        return Optional.ofNullable(owners.get(uuid));
    }

    public Optional<T> isWalletOwner(Player player) {
        return isWalletOwner(player.getUniqueId());
    }

    private void saveAll() {
        owners.values().forEach(walletOwner -> sqlCrudManager.update(walletOwner.serializeAllAttributes()));
    }

    protected CompletableFuture<T> read(UUID uuid) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () ->
                future.complete(walletOwner.apply(sqlCrudManager.read(uuid.toString()))));
        return future;
    }

    /**
     * Registers the BlobEconomy for this plugin.
     *
     * @param defaultCurrency The default currency for this economy.
     * @return The provider for the Economy class in case you
     * later want to use it.
     */
    @NotNull
    public BlobEconomy<T> registerEconomy(Currency defaultCurrency, ObjectDirector<Currency> currencyDirector) {
        if (registeredEconomy)
            throw new IllegalStateException("BlobPlugin already registered their BlobEconomy");
        registeredEconomy = true;
        this.defaultCurrency = defaultCurrency.getKey();
        this.currencyDirector = currencyDirector;
        return new BlobEconomy<>(this);
    }

    @Nullable
    public Currency getDefaultCurrency() {
        return currencyDirector.getObjectManager().getObject(defaultCurrency);
    }

    public void setDefaultCurrency(Currency defaultCurrency) {
        this.defaultCurrency = defaultCurrency.getKey();
    }

    /**
     * Registers the BlobEconomy commands for this plugin.
     *
     * @param currencyDirector     The director for the currencies.
     * @param commandName          The name of the command.
     * @param depositArgumentName  The name of the deposit argument.
     * @param withdrawArgumentName The name of the withdrawal argument.
     * @param setArgumentName      The name of the set argument.
     * @param resetArgumentName    The name of the reset argument.
     */
    public void registerBlobEconomyCommand(ObjectDirector<Currency> currencyDirector,
                                           String commandName,
                                           String depositArgumentName,
                                           String withdrawArgumentName,
                                           String setArgumentName,
                                           String resetArgumentName) {
        if (!registeredEconomy)
            throw new IllegalStateException("BlobPlugin has not registered their BlobEconomy");
        BlobEconomyCommand<T> command = new BlobEconomyCommand<>(this, currencyDirector.getObjectManager(),
                commandName, depositArgumentName, withdrawArgumentName, setArgumentName, resetArgumentName);
    }

    /**
     * Registers the BlobEconomy commands for this plugin.
     * The command name is "eco", the deposit argument name is "give",
     * the withdrawal argument name is "take", the set argument name is "set",
     * and the reset argument name is "reset".
     * <p>
     * Remember to register "eco" inside your plugin.yml file!
     *
     * @param currencyDirector The director for the currencies.
     */
    public void registerDefaultEconomyCommand(ObjectDirector<Currency> currencyDirector) {
        registerBlobEconomyCommand(currencyDirector, "eco",
                "give", "take",
                "set", "reset");
    }
}