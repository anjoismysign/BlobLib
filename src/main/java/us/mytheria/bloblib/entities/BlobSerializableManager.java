package us.mytheria.bloblib.entities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;
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

public class BlobSerializableManager<T extends BlobSerializable> extends Manager implements Listener {
    protected final HashMap<UUID, T> serializables;
    private final HashSet<UUID> saving;
    protected BlobCrudManager<BlobCrudable> crudManager;
    private final BlobPlugin plugin;
    private final Function<BlobCrudable, T> generator;
    private final @Nullable Function<T, Event> joinEvent;
    private final @Nullable Function<T, Event> quitEvent;

    protected BlobSerializableManager(ManagerDirector managerDirector, Function<BlobCrudable, BlobCrudable> newBorn,
                                      Function<BlobCrudable, T> generator,
                                      String crudableName, boolean logActivity,
                                      @Nullable Function<T, Event> joinEvent,
                                      @Nullable Function<T, Event> quitEvent) {
        super(managerDirector);
        plugin = managerDirector.getPlugin();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        serializables = new HashMap<>();
        this.generator = generator;
        this.joinEvent = joinEvent;
        this.quitEvent = quitEvent;
        saving = new HashSet<>();
        crudManager = BlobCrudManagerFactory.PLAYER(plugin, crudableName, newBorn, logActivity);
        reload();
    }

    @Override
    public void unload() {
        saveAll();
    }

    @Override
    public void reload() {
        unload();
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
            if (player == null || !player.isOnline()) {
                future.completeExceptionally(new NullPointerException("Player is null"));
                return;
            }
            T applied = this.generator.apply(crudManager.read(uuid.toString()));
            serializables.put(uuid, applied);
            future.complete(applied);
        });
        future.thenAccept(serializable -> {
            if (joinEvent == null)
                return;
            Bukkit.getPluginManager().callEvent(joinEvent.apply(serializable));
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Optional<T> optional = isBlobSerializable(uuid);
        if (optional.isEmpty())
            return;
        T serializable = optional.get();
        if (quitEvent != null)
            Bukkit.getPluginManager().callEvent(quitEvent.apply(serializable));
        saving.add(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            crudManager.update(serializable.serializeAllAttributes());
            removeObject(uuid);
            saving.remove(uuid);
        });
    }

    public void addObject(UUID key, T serializable) {
        serializables.put(key, serializable);
    }

    public void addObject(Player player, T serializable) {
        addObject(player.getUniqueId(), serializable);
    }

    public void removeObject(UUID key) {
        serializables.remove(key);
    }

    public void removeObject(Player player) {
        removeObject(player.getUniqueId());
    }

    public Optional<T> isBlobSerializable(UUID uuid) {
        return Optional.ofNullable(serializables.get(uuid));
    }

    public Optional<T> isBlobSerializable(Player player) {
        return isBlobSerializable(player.getUniqueId());
    }

    public void ifIsOnline(UUID uuid, Consumer<T> consumer) {
        Optional<T> optional = isBlobSerializable(uuid);
        optional.ifPresent(consumer);
    }

    public void ifIsOnlineThenUpdateElse(UUID uuid, Consumer<T> consumer,
                                         Runnable runnable) {
        Optional<T> optional = isBlobSerializable(uuid);
        boolean isPresent = optional.isPresent();
        if (isPresent) {
            T serializable = optional.get();
            consumer.accept(serializable);
            crudManager.update(serializable.serializeAllAttributes());
        } else {
            runnable.run();
        }
    }

    public void ifIsOnlineThenUpdate(UUID uuid, Consumer<T> consumer) {
        ifIsOnlineThenUpdateElse(uuid, consumer, () -> {
        });
    }

    public CompletableFuture<T> readAsynchronously(String key) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () ->
                future.complete(generator.apply(crudManager.read(key))));
        return future;
    }

    public void readThenUpdate(String key, Consumer<T> consumer) {
        T serializable = generator.apply(crudManager.read(key));
        consumer.accept(serializable);
        crudManager.update(serializable.serializeAllAttributes());
    }

    public boolean exists(String key) {
        return crudManager.exists(key);
    }

    private void saveAll() {
        serializables.values().forEach(serializable -> crudManager.update(serializable.serializeAllAttributes()));
    }

    public Collection<T> getAll() {
        return serializables.values();
    }

    public StorageType getStorageType() {
        return crudManager.getStorageType();
    }

    public IdentifierType getIdentifierType() {
        return crudManager.getIdentifierType();
    }
}
