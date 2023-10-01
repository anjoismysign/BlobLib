package us.mytheria.bloblib.entities.currency;

import net.milkbowl.vault.economy.IdentityEconomy;
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
import us.mytheria.bloblib.storage.BlobCrudManager;
import us.mytheria.bloblib.storage.IdentifierType;
import us.mytheria.bloblib.storage.StorageType;
import us.mytheria.bloblib.utilities.BlobCrudManagerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WalletOwnerManager<T extends WalletOwner> extends Manager implements Listener {
    protected final HashMap<UUID, T> walletOwners;
    private final HashSet<UUID> saving;
    protected BlobCrudManager<BlobCrudable> crudManager;
    private final BlobPlugin plugin;
    private final Function<BlobCrudable, T> generator;
    private final @Nullable Function<T, Event> joinEvent;
    private final @Nullable Function<T, Event> quitEvent;
    private boolean registeredEconomy, registeredPAPI;
    private String defaultCurrency;
    protected ObjectDirector<Currency> currencyDirector;
    @Nullable
    private EconomyPHExpansion<T> economyPHExpansion;
    private HashMap<String, CurrencyEconomy> implementations;

    protected WalletOwnerManager(ManagerDirector managerDirector, Function<BlobCrudable, BlobCrudable> newBorn,
                                 Function<BlobCrudable, T> generator,
                                 String crudableName, boolean logActivity,
                                 @Nullable Function<T, Event> joinEvent,
                                 @Nullable Function<T, Event> quitEvent) {
        super(managerDirector);
        plugin = managerDirector.getPlugin();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        walletOwners = new HashMap<>();
        this.generator = generator;
        this.joinEvent = joinEvent;
        this.quitEvent = quitEvent;
        this.registeredEconomy = false;
        saving = new HashSet<>();
        crudManager = BlobCrudManagerFactory.PLAYER(plugin, crudableName, newBorn, logActivity);
        saveAll();
    }

    @Override
    public void unload() {
        saveAll();
    }

    @Override
    public void reload() {
        unload();
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), this::updateImplementations);
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
        CompletableFuture<BlobCrudable> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (player == null || !player.isOnline()) {
                future.completeExceptionally(new NullPointerException("Player is null"));
                return;
            }
            BlobCrudable crudable = crudManager.read(uuid.toString());
            future.complete(crudable);
        });
        future.thenAccept(crudable -> {
            if (player == null || !player.isOnline())
                return;
            T applied = generator.apply(crudable);
            walletOwners.put(uuid, applied);
            if (joinEvent == null)
                return;
            Bukkit.getPluginManager().callEvent(joinEvent.apply(applied));
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
        BlobCrudable crudable = walletOwner.serializeAllAttributes();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            crudManager.update(crudable);
            removeObject(uuid);
            saving.remove(uuid);
        });
    }

    public void addObject(UUID key, T walletOwner) {
        walletOwners.put(key, walletOwner);
    }

    public void addObject(Player player, T walletOwner) {
        addObject(player.getUniqueId(), walletOwner);
    }

    public void removeObject(UUID key) {
        walletOwners.remove(key);
    }

    public void removeObject(Player player) {
        removeObject(player.getUniqueId());
    }

    public Optional<T> isWalletOwner(UUID uuid) {
        return Optional.ofNullable(walletOwners.get(uuid));
    }

    public Optional<T> isWalletOwner(Player player) {
        return isWalletOwner(player.getUniqueId());
    }

    public void ifIsOnline(UUID uuid, Consumer<T> consumer) {
        Optional<T> optional = isWalletOwner(uuid);
        optional.ifPresent(consumer);
    }

    public void ifIsOnlineThenUpdateElse(UUID uuid, Consumer<T> consumer,
                                         Runnable runnable) {
        Optional<T> optional = isWalletOwner(uuid);
        boolean isPresent = optional.isPresent();
        if (isPresent) {
            T walletOwner = optional.get();
            consumer.accept(walletOwner);
            crudManager.update(walletOwner.serializeAllAttributes());
        } else {
            runnable.run();
        }
    }

    public void ifIsOnlineThenUpdate(UUID uuid, Consumer<T> consumer) {
        ifIsOnlineThenUpdateElse(uuid, consumer, () -> {
        });
    }

    private void saveAll() {
        walletOwners.values().forEach(walletOwner -> crudManager.update(walletOwner.serializeAllAttributes()));
    }

    public CompletableFuture<T> readAsynchronously(String key) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () ->
                future.complete(generator.apply(crudManager.read(key))));
        return future;
    }

    public void readThenUpdate(String key, Consumer<T> consumer) {
        T walletOwner = generator.apply(crudManager.read(key));
        consumer.accept(walletOwner);
        crudManager.update(walletOwner.serializeAllAttributes());
    }

    public boolean exists(String key) {
        return crudManager.exists(key);
    }

    public Collection<T> getAll() {
        return walletOwners.values();
    }

    /**
     * Registers the BlobEconomy for this plugin.
     *
     * @param defaultCurrency  The default currency for this economy.
     * @param currencyDirector The director for the currencies.
     * @param force            if true, will override vault economy providers
     * @return The provider for the Economy class in case you
     * later want to use it.
     */
    @NotNull
    public BlobEconomy<T> registerEconomy(Currency defaultCurrency,
                                          ObjectDirector<Currency> currencyDirector,
                                          boolean force) {
        if (registeredEconomy)
            throw new IllegalStateException("BlobPlugin already registered their BlobEconomy");
        registeredEconomy = true;
        this.defaultCurrency = Objects.requireNonNull(defaultCurrency).getKey();
        this.currencyDirector = currencyDirector;
        return new BlobEconomy<>(this, force);
    }

    public BlobEconomy<T> registerEconomy(Currency defaultCurrency,
                                          ObjectDirector<Currency> currencyDirector) {
        return registerEconomy(defaultCurrency, currencyDirector, true);
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

    @Nullable
    public EconomyPHExpansion<T> registerPlaceholderAPIExpansion() {
        if (registeredPAPI)
            throw new IllegalStateException("BlobPlugin already registered their PlaceholderAPI expansion");
        if (!registeredEconomy)
            throw new IllegalStateException("BlobPlugin has not registered their BlobEconomy");
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getPlugin().getAnjoLogger().log("PlaceholderAPI not found, not registering PlaceholderAPI expansion for " + getPlugin().getName());
            return null;
        }
        registeredPAPI = true;
        EconomyPHExpansion<T> expansion = new EconomyPHExpansion<>(this);
        CompletableFuture<Void> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTask(getPlugin(), () -> {
            expansion.register();
            future.complete(null);
        });
        future.join();
        economyPHExpansion = expansion;
        return expansion;
    }

    public WalletOwnerManager<T> unregisterPlaceholderAPIExpansion() {
        if (!registeredPAPI)
            throw new IllegalStateException("BlobPlugin has not registered their PlaceholderAPI expansion");
        economyPHExpansion.unregister();
        economyPHExpansion = null;
        registeredPAPI = false;
        return this;
    }

    /**
     * Will get an implementation of the CurrencyEconomy class.
     * If the implementation doesn't exist, it will
     * warn through console and return null.
     *
     * @param key The key of the implementation.
     * @return The implementation.
     */
    @Nullable
    public CurrencyEconomy getImplementation(String key) {
        CurrencyEconomy economy = implementations.get(key);
        if (economy == null) {
            getPlugin().getAnjoLogger().singleError("Implementation doesn't exist. Currently available " +
                    "implementations: " + Arrays.toString(implementations.values().stream()
                    .map(CurrencyEconomy::getName)
                    .toArray()));
        }
        return economy;
    }

    public Collection<IdentityEconomy> retrieveImplementations() {
        return implementations.values()
                .stream()
                .map(currencyEconomy -> (IdentityEconomy) currencyEconomy)
                .collect(Collectors.toList());
    }

    private HashMap<String, CurrencyEconomy> convertAll() {
        HashMap<String, CurrencyEconomy> map = new HashMap<>();
        currencyDirector.getObjectManager().values().stream()
                .map(currency -> new CurrencyEconomy(currency, this))
                .forEach(currencyEconomy -> map.put(currencyEconomy.getName(), currencyEconomy));
        return map;
    }

    /**
     * Updates the implementations of the BlobEconomy.
     */
    protected void updateImplementations() {
        if (!registeredEconomy) {
            throw new IllegalStateException("BlobPlugin has not registered their BlobEconomy");
        }
        this.implementations = convertAll();
    }

    public StorageType getStorageType() {
        return crudManager.getStorageType();
    }

    public IdentifierType getIdentifierType() {
        return crudManager.getIdentifierType();
    }
}
