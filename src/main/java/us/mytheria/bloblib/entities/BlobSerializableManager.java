package us.mytheria.bloblib.entities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
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

public class BlobSerializableManager<T extends BlobSerializable> extends Manager implements BlobSerializableHandler {
    protected final Map<UUID, T> serializables;
    private final Map<UUID, BukkitTask> autoSave;
    private final Set<UUID> saving;
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
        this(managerDirector, newBorn, generator, crudableName, logActivity,
                joinEvent, quitEvent, EventPriority.NORMAL, EventPriority.NORMAL);
    }

    protected BlobSerializableManager(ManagerDirector managerDirector, Function<BlobCrudable, BlobCrudable> newBorn,
                                      Function<BlobCrudable, T> generator,
                                      String crudableName, boolean logActivity,
                                      @Nullable Function<T, Event> joinEvent,
                                      @Nullable Function<T, Event> quitEvent,
                                      @Nullable EventPriority joinPriority,
                                      @Nullable EventPriority quitPriority) {
        super(managerDirector);
        plugin = managerDirector.getPlugin();
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(this, plugin);
        if (joinPriority != null)
            registerJoinListener(pluginManager, joinPriority);
        if (quitPriority != null)
            registerQuitListener(pluginManager, quitPriority);
        serializables = new HashMap<>();
        autoSave = new HashMap<>();
        this.generator = generator;
        this.joinEvent = joinEvent;
        this.quitEvent = quitEvent;
        saving = new HashSet<>();
        crudManager = BlobCrudManagerFactory.PLAYER(plugin, crudableName, newBorn, logActivity);
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

    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (player == null || !player.isOnline())
                return;
            BlobCrudable crudable = crudManager.read(uuid.toString());
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (player == null || !player.isOnline())
                    return;
                T applied = generator.apply(crudable);
                BlobCrudable serialized = applied.serializeAllAttributes();
                Bukkit.getScheduler().runTaskAsynchronously(getPlugin(),
                        () -> crudManager.update(serialized));
                serializables.put(uuid, applied);
                if (joinEvent == null)
                    return;
                Bukkit.getPluginManager().callEvent(joinEvent.apply(applied));
                autoSave.put(uuid, new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player == null || !player.isOnline()) {
                            cancel();
                            return;
                        }
                        BlobCrudable serialized = applied.serializeAllAttributes();
                        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(),
                                () -> crudManager.update(serialized));
                    }
                }.runTaskTimer(getPlugin(), 20 * 60 * 5,
                        20 * 60 * 5));
            });
        });
    }

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
        autoSave.remove(uuid);
        BlobCrudable crudable = serializable.serializeAllAttributes();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            crudManager.update(crudable);
            removeObject(uuid);
            saving.remove(uuid);
        });
    }

    public void addSaving(UUID uuid) {
        saving.add(uuid);
    }

    public void removeSaving(UUID uuid) {
        saving.remove(uuid);
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

    public void update(BlobCrudable crudable) {
        crudManager.update(crudable);
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
