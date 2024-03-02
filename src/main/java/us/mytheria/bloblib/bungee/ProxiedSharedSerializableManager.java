package us.mytheria.bloblib.bungee;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.BlobCrudable;
import us.mytheria.bloblib.entities.DocumentDecorator;
import us.mytheria.bloblib.entities.SharedSerializable;
import us.mytheria.bloblib.managers.BlobPlugin;
import us.mytheria.bloblib.managers.Manager;
import us.mytheria.bloblib.managers.ManagerDirector;
import us.mytheria.bloblib.storage.BlobCrudManager;
import us.mytheria.bloblib.storage.IdentifierType;
import us.mytheria.bloblib.storage.StorageType;
import us.mytheria.bloblib.utilities.BlobCrudManagerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProxiedSharedSerializableManager<T extends SharedSerializable<?>>
        extends Manager implements Listener, PluginMessageListener {
    protected final Map<String, T> cache;
    protected BlobCrudManager<BlobCrudable> crudManager;
    private final BlobPlugin plugin;
    private final String tag;
    private final Function<BlobCrudable, T> generator;
    private final @Nullable Function<T, Event> joinEvent;
    private final @Nullable Function<T, Event> quitEvent;

    protected ProxiedSharedSerializableManager(ManagerDirector managerDirector, Function<BlobCrudable, BlobCrudable> newBorn,
                                               Function<BlobCrudable, T> generator,
                                               String crudableName, boolean logActivity,
                                               @Nullable Function<T, Event> joinEvent,
                                               @Nullable Function<T, Event> quitEvent) {
        super(managerDirector);
        plugin = managerDirector.getPlugin();
        tag = plugin.getName() + "-" + crudableName;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, tag);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, tag, this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        cache = new HashMap<>();
        this.generator = generator;
        this.joinEvent = joinEvent;
        this.quitEvent = quitEvent;
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

/*
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
            serializables.put(uuid, applied);
            if (joinEvent == null)
                return;
            Bukkit.getPluginManager().callEvent(joinEvent.apply(applied));
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Optional<T> optional = isSharedSerializable(uuid);
        if (optional.isEmpty())
            return;
        T serializable = optional.get();
        if (quitEvent != null)
            Bukkit.getPluginManager().callEvent(quitEvent.apply(serializable));
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            crudManager.update(serializable.serializeAllAttributes());
            removeObject(uuid);
            //Send Quit message
        });
    }
*/

    @Override
    public void onPluginMessageReceived(@NotNull String channel,
                                        @NotNull Player x,
                                        byte[] bytes) {
        DocumentDecorator decorator = DocumentDecorator.deserialize(bytes);
        Document document = decorator.document();
        String action = document.getString("Action").toLowerCase();
        switch (action) {
            case "initialize" -> {
                String playerName = decorator.hasString("SharedSerializable#OwnerName")
                        .orElseThrow();
                Player player = Bukkit.getPlayer(playerName);
                if (player == null)
                    throw new NullPointerException("Player not found, report to BlobLib developers!");
                String instanceId = decorator.hasString("SharedSerializable#UniqueId")
                        .orElseThrow();
                CompletableFuture<BlobCrudable> future = new CompletableFuture<>();
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    if (player == null || !player.isOnline()) {
                        future.completeExceptionally(new NullPointerException("Player is null"));
                        return;
                    }
                    BlobCrudable crudable = crudManager.read(instanceId);
                    future.complete(crudable);
                });
                future.thenAccept(crudable -> {
                    if (player == null || !player.isOnline())
                        return;
                    T applied = generator.apply(crudable);
                    cache.put(instanceId, applied);
                    //Send instantiationConfirmed message
                    Document send = new Document();
                    send.put("Action", "instantiationConfirmed");
                    send.put("ProxiedPlayer#UniqueName", player.getName());
                    applied.join(player);
                    sendPluginMessage(player, send);
                    //Address JoinEvent
                    if (joinEvent == null)
                        return;
                    Bukkit.getPluginManager().callEvent(joinEvent.apply(applied));
                });
            }
            case "join" -> {
                String playerName = decorator.hasString("SharedSerializable#OwnerName")
                        .orElseThrow();
                Player player = Bukkit.getPlayer(playerName);
                if (player == null)
                    throw new NullPointerException("Player not found, report to BlobLib developers!");
                String instanceId = decorator.hasString("SharedSerializable#UniqueId")
                        .orElseThrow();
                T instance = cache.get(instanceId);
                if (instance == null)
                    throw new NullPointerException("Instance not found, report to BlobLib developers!");
                instance.join(player);
                //Send instantiationConfirmed message
                Document send = new Document();
                send.put("Action", "instantiationConfirmed");
                send.put("ProxiedPlayer#UniqueName", player.getName());
                instance.join(player);
                sendPluginMessage(player, send);
                //Address JoinEvent
                if (joinEvent == null)
                    return;
                Bukkit.getPluginManager().callEvent(joinEvent.apply(instance));
            }
        }
    }

    private void sendPluginMessage(Player player, Document document) {
        player.sendPluginMessage(plugin, tag, new DocumentDecorator(document).serialize());
    }

    /**
     * Adds the object to the cache.
     *
     * @param serializable The object to add.
     * @return The previous object. Null if no previous object was mapped.
     */
    @Nullable
    public T addObject(T serializable) {
        return cache.put(serializable.getIdentification(), serializable);
    }

    /**
     * Removes the object from the cache.
     *
     * @param serializable The object to remove.
     * @return The object removed. Null if not found.
     */
    @Nullable
    public T removeObject(T serializable) {
        return removeObject(serializable.getIdentification());
    }

    /**
     * Removes the object from the cache.
     *
     * @param key The key of the object.
     * @return The object removed. Null if not found.
     */
    @Nullable
    public T removeObject(String key) {
        return cache.remove(key);
    }

    public Optional<T> isSharedSerializable(String id) {
        return Optional.ofNullable(cache.get(id));
    }

    public void ifIsOnline(String id, Consumer<T> consumer) {
        Optional<T> optional = isSharedSerializable(id);
        optional.ifPresent(consumer);
    }

    public void ifIsOnlineThenUpdateElse(String id, Consumer<T> consumer,
                                         Runnable runnable) {
        Optional<T> optional = isSharedSerializable(id);
        boolean isPresent = optional.isPresent();
        if (isPresent) {
            T serializable = optional.get();
            consumer.accept(serializable);
            crudManager.update(serializable.serializeAllAttributes());
        } else {
            runnable.run();
        }
    }

    public void ifIsOnlineThenUpdate(String id, Consumer<T> consumer) {
        ifIsOnlineThenUpdateElse(id, consumer, () -> {
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
        cache.values().forEach(serializable -> crudManager.update(serializable.serializeAllAttributes()));
    }

    public Collection<T> getAll() {
        return cache.values();
    }

    public StorageType getStorageType() {
        return crudManager.getStorageType();
    }

    public IdentifierType getIdentifierType() {
        return crudManager.getIdentifierType();
    }
}
