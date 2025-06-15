package io.github.anjoismysign.bloblib.bungee;

import io.github.anjoismysign.bloblib.entities.BlobCrudable;
import io.github.anjoismysign.bloblib.entities.DocumentDecorator;
import io.github.anjoismysign.bloblib.entities.SharedSerializable;
import io.github.anjoismysign.bloblib.managers.BlobPlugin;
import io.github.anjoismysign.bloblib.managers.Manager;
import io.github.anjoismysign.bloblib.managers.ManagerDirector;
import io.github.anjoismysign.bloblib.storage.BlobCrudManager;
import io.github.anjoismysign.bloblib.storage.IdentifierType;
import io.github.anjoismysign.bloblib.storage.StorageType;
import io.github.anjoismysign.bloblib.utilities.BlobCrudManagerFactory;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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

    protected ProxiedSharedSerializableManager(ManagerDirector managerDirector,
                                               Function<BlobCrudable, BlobCrudable> newBorn,
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

    @Override
    public void onPluginMessageReceived(@NotNull String channel,
                                        @NotNull Player x,
                                        byte[] bytes) {
        DocumentDecorator decorator = DocumentDecorator.deserialize(bytes);
        Document document = decorator.document();
        String action = document.getString("Action").toLowerCase(Locale.ROOT);
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
                UUID uuid = player.getUniqueId();
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    if (player != Bukkit.getPlayer(uuid)) {
                        future.completeExceptionally(new NullPointerException("Player is null"));
                        return;
                    }
                    BlobCrudable crudable = crudManager.read(instanceId);
                    future.complete(crudable);
                });
                future.thenAccept(crudable -> {
                    if (player != Bukkit.getPlayer(uuid))
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
