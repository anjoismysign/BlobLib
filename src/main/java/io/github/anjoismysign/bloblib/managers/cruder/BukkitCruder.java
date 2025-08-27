package io.github.anjoismysign.bloblib.managers.cruder;

import io.github.anjoismysign.bloblib.entities.BlobSerializableHandler;
import io.github.anjoismysign.psa.crud.Crudable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class BukkitCruder<T extends Crudable> implements BlobSerializableHandler {
    protected final Map<UUID, T> serializables;
    private final Map<UUID, BukkitTask> autoSave;
    private final Set<UUID> saving;
    protected Cruder<T> cruder;
    private final JavaPlugin plugin;
    private final @Nullable Function<T, Event> joinEvent;
    private final @Nullable Function<T, Event> quitEvent;
    private final @Nullable Consumer<T> onRead;
    private final @Nullable Consumer<T> onUpdate;
    private final @Nullable Consumer<T> onAutoSave;
    private final @Nullable Consumer<T> onQuit;

    protected BukkitCruder(JavaPlugin plugin,
                           Class<T> clazz,
                           Function<String, T> createFunction,
                           @Nullable Function<T, Event> joinEvent,
                           @Nullable Function<T, Event> quitEvent,
                           @Nullable EventPriority joinPriority,
                           @Nullable EventPriority quitPriority,
                           @Nullable Consumer<T> onRead,
                           @Nullable Consumer<T> onUpdate,
                           @Nullable Consumer<T> onAutoSave,
                           @Nullable Consumer<T> onQuit) {
        this.plugin = plugin;
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(this, plugin);
        if (joinPriority != null)
            registerJoinListener(pluginManager, joinPriority);
        if (quitPriority != null)
            registerQuitListener(pluginManager, quitPriority);
        serializables = new HashMap<>();
        autoSave = new HashMap<>();
        this.joinEvent = joinEvent;
        this.quitEvent = quitEvent;
        saving = new HashSet<>();
        cruder = Cruder.of(plugin, clazz, createFunction);
        this.onRead = onRead;
        this.onUpdate = onUpdate;
        this.onAutoSave = onAutoSave;
        this.onQuit = onQuit;
        loadAll();
    }

    @EventHandler
    public void onPrejoin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        if (saving.contains(uuid))
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You are already saving your data, please try again in a few seconds.");
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }

    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (Bukkit.getPlayer(uuid) == null){
                return;
                }
            T serializable = cruder.readOrGenerate(uuid.toString());
            if (onRead != null) {
                onRead.accept(serializable);
            }
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (Bukkit.getPlayer(uuid) == null) {
                    return;
                }
                Bukkit.getScheduler().runTaskAsynchronously(getPlugin(),
                        () -> cruder.update(serializable));
                serializables.put(uuid, serializable);
                if (joinEvent == null)
                    return;
                Bukkit.getPluginManager().callEvent(joinEvent.apply(serializable));
                autoSave.put(uuid, new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (Bukkit.getPlayer(uuid) == null) {
                            cancel();
                            return;
                        }
                        if (onAutoSave != null){
                            onAutoSave.accept(serializable);
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(),
                                () -> {
                                    cruder.update(serializable);
                                    if (onUpdate != null) {
                                        onUpdate.accept(serializable);
                                    }
                                });
                    }
                }.runTaskTimer(getPlugin(), 20 * 60 * 5,
                        20 * 60 * 5));
            });
        });
    }

    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Optional<T> optional = isCrudable(uuid);
        if (optional.isEmpty())
            return;
        T serializable = optional.get();
        if (quitEvent != null)
            Bukkit.getPluginManager().callEvent(quitEvent.apply(serializable));
        saving.add(uuid);
        autoSave.remove(uuid);
        if (onQuit != null){
            onQuit.accept(serializable);
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            cruder.update(serializable);
            if (onUpdate != null) {
                onUpdate.accept(serializable);
            }
            removeObject(uuid);
            saving.remove(uuid);
        });
    }

    @Nullable
    public T lookFor(@NotNull Player player){
        Objects.requireNonNull(player, "'player' cannot be null");
        UUID uuid = player.getUniqueId();
        return serializables.get(uuid);
    }

    public T get(@NotNull Player player){
        return Objects.requireNonNull(lookFor(player), player.getName() + " doesn't seem to be cached");
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

    public Optional<T> isCrudable(UUID uuid) {
        return Optional.ofNullable(serializables.get(uuid));
    }

    public Optional<T> isCrudable(Player player) {
        return isCrudable(player.getUniqueId());
    }

    public void ifIsOnline(UUID uuid, Consumer<T> consumer) {
        Optional<T> optional = isCrudable(uuid);
        optional.ifPresent(consumer);
    }

    public void ifIsOnlineThenUpdateElse(UUID uuid, Consumer<T> consumer,
                                         Runnable runnable) {
        Optional<T> optional = isCrudable(uuid);
        boolean isPresent = optional.isPresent();
        if (isPresent) {
            T serializable = optional.get();
            consumer.accept(serializable);
            cruder.update(serializable);
            if (onUpdate != null) {
                onUpdate.accept(serializable);
            }
        } else {
            runnable.run();
        }
    }

    public void ifIsOnlineThenUpdate(UUID uuid, Consumer<T> consumer) {
        ifIsOnlineThenUpdateElse(uuid, consumer, () -> {
        });
    }

    public CompletableFuture<T> readAsynchronously(String identification) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            T serializable = cruder.readOrGenerate(identification);
            if (onUpdate != null) {
                onUpdate.accept(serializable);
            }
            future.complete(serializable);
        });
        return future;
    }

    public void readThenUpdate(String identification, Consumer<T> consumer) {
        T serializable = cruder.readOrGenerate(identification);
        consumer.accept(serializable);
        cruder.update(serializable);
        if (onUpdate != null) {
            onUpdate.accept(serializable);
        }
    }

    public void loadAll(){
        Bukkit.getOnlinePlayers().forEach(player -> {
            UUID uuid = player.getUniqueId();
            String identification = uuid.toString();
            T serializable = cruder.readOrGenerate(identification);
            cruder.update(serializable);

            autoSave.put(uuid, new BukkitRunnable() {
                @Override
                public void run() {
                    if (Bukkit.getPlayer(uuid) == null) {
                        cancel();
                        return;
                    }
                    Bukkit.getScheduler().runTaskAsynchronously(getPlugin(),
                            () -> {
                                cruder.update(serializable);
                                if (onUpdate != null) {
                                    onUpdate.accept(serializable);
                                }
                            });
                }
            }.runTaskTimer(getPlugin(), 20 * 60 * 5,
                    20 * 60 * 5));
        });
    }

    public boolean exists(String identification) {
        return cruder.exists(identification);
    }

    public void saveAll() {
        serializables.values().forEach(serializable -> {
            cruder.update(serializable);
            if (onUpdate != null) {
                onUpdate.accept(serializable);
            }
        });
    }

    public Collection<T> getAll() {
        return serializables.values();
    }

}
