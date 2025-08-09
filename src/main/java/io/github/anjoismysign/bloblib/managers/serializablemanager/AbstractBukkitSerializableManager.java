package io.github.anjoismysign.bloblib.managers.serializablemanager;

import io.github.anjoismysign.bloblib.entities.BlobScheduler;
import io.github.anjoismysign.bloblib.entities.PermissionDecorator;
import io.github.anjoismysign.bloblib.managers.BlobPlugin;
import io.github.anjoismysign.psa.crud.CrudDatabaseCredentials;
import io.github.anjoismysign.psa.crud.DatabaseCredentials;
import io.github.anjoismysign.psa.lehmapp.LehmappCrudable;
import io.github.anjoismysign.psa.lehmapp.LehmappSerializable;
import io.github.anjoismysign.psa.serializablemanager.SimpleSerializableManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractBukkitSerializableManager
        <T extends LehmappSerializable, S extends BukkitSerializableEvent<T>>
        extends SimpleSerializableManager<T>
        implements BukkitSerializableManager<T>, Listener {
    private final @Nullable Listener joinEvent;
    private final @Nullable Listener quitEvent;
    private final @NotNull JavaPlugin plugin;
    private final @Nullable Supplier<Boolean> eventsRegistrationSupplier;
    private final @NotNull BlobScheduler scheduler;
    private final @NotNull PermissionDecorator permissionDecorator;
    private final Function<Player, String> identifier = identifying -> databaseCredentials.getIdentifier() == DatabaseCredentials.Identifier.UUID ? identifying.getUniqueId().toString() : identifying.getName();

    public AbstractBukkitSerializableManager(
            @NotNull CrudDatabaseCredentials credentials,
            @NotNull Function<LehmappCrudable, T> deserializer,
            @Nullable Function<T, S> joinEvent,
            @Nullable Function<T, S> quitEvent,
            @NotNull JavaPlugin plugin,
            @Nullable Supplier<Boolean> eventsRegistrationSupplier) {
        super(credentials, deserializer);
        scheduler = new BlobScheduler(plugin);
        permissionDecorator = new PermissionDecorator() {
            Permission permission;
            @Override
            public @NotNull Permission getPermission() {
                if (permission == null)
                    permission = new Permission(plugin.getName());
                return permission;
            }
        };
        Bukkit.getOnlinePlayers().forEach(player -> {
            String id = identifier.apply(player);
            scheduler.async(() -> {
                LehmappCrudable crudable = controller.getCrudManager().read(id);
                scheduler.sync(() -> {
                    T constructed = deserializer.apply(crudable);
                    if (joinEvent != null) {
                        S serializableEvent = joinEvent.apply(constructed);
                        Bukkit.getPluginManager().callEvent(serializableEvent);
                    }
                    map().put(id, constructed);
                });
            });
        });
        this.joinEvent = joinEvent == null ? null : new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                Player player = event.getPlayer();
                String id = identifier.apply(player);
                BlobScheduler scheduler = getScheduler();
                scheduler.async(() -> {
                    LehmappCrudable crudable = controller.getCrudManager().read(id);
                    scheduler.sync(() -> {
                        T constructed = deserializer.apply(crudable);
                        S serializableEvent = joinEvent.apply(constructed);
                        Bukkit.getPluginManager().callEvent(serializableEvent);
                        map().put(id, constructed);
                    });
                });
            }
        };
        this.quitEvent = quitEvent == null ? null : new Listener() {
            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                Player player = event.getPlayer();
                String id = identifier.apply(player);
                T stored = map().remove(id);
                if (stored == null)
                    return;
                S serializableEvent = quitEvent.apply(stored);
                Bukkit.getPluginManager().callEvent(serializableEvent);
                LehmappCrudable crudable = stored.serialize();
                getScheduler().async(() -> {
                    controller.getCrudManager().update(crudable);
                });
            }
        };
        this.plugin = plugin;
        this.eventsRegistrationSupplier = eventsRegistrationSupplier;
        if (eventsRegistrationSupplier == null || !eventsRegistrationSupplier.get())
            registerEvents(true);
    }

    public void reloadEvents() {
        if (eventsRegistrationSupplier == null || !eventsRegistrationSupplier.get())
            return;
        registerEvents(eventsRegistrationSupplier.get());
    }

    public void unregisterEvents() {
        if (joinEvent != null)
            HandlerList.unregisterAll(joinEvent);
        if (quitEvent != null)
            HandlerList.unregisterAll(quitEvent);
    }

    public T lookFor(@NotNull Player player) {
        Objects.requireNonNull(player, "'player' cannot be null...");
        String id = identifier.apply(player);
        return cacheLook(id);
    }

    private void registerEvents(boolean register) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (joinEvent != null) {
            HandlerList.unregisterAll(joinEvent);
            if (!register)
                return;
            pluginManager.registerEvents(joinEvent, plugin);
        }
        if (quitEvent != null) {
            HandlerList.unregisterAll(quitEvent);
            if (!register)
                return;
            pluginManager.registerEvents(quitEvent, plugin);
        }
    }

    public void syncSaveAll() {
        map().values().forEach(serializable -> {
            controller.getCrudManager().update(serializable.serialize());
        });
    }

    @Override
    public @NotNull BlobScheduler getScheduler(){
        return scheduler;
    }

    @Override
    public @NotNull PermissionDecorator getPermissionDecorator(){
        return permissionDecorator;
    }
}
