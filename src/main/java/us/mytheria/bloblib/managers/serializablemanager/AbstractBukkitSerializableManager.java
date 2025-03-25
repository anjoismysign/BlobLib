package us.mytheria.bloblib.managers.serializablemanager;

import me.anjoismysign.psa.crud.CrudDatabaseCredentials;
import me.anjoismysign.psa.crud.DatabaseCredentials;
import me.anjoismysign.psa.lehmapp.LehmappCrudable;
import me.anjoismysign.psa.lehmapp.LehmappSerializable;
import me.anjoismysign.psa.serializablemanager.SimpleSerializableManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.BlobScheduler;
import us.mytheria.bloblib.managers.BlobPlugin;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractBukkitSerializableManager
        <T extends LehmappSerializable, S extends BukkitSerializableEvent<T>>
        extends SimpleSerializableManager<T>
        implements BukkitSerializableManager<T>, Listener {
    private final @Nullable Listener joinEvent;
    private final @Nullable Listener quitEvent;
    private final @NotNull BlobPlugin plugin;
    private final @Nullable Supplier<Boolean> eventsRegistrationSupplier;
    private final Function<Player, String> identifier = identifying -> databaseCredentials.getIdentifier() == DatabaseCredentials.Identifier.UUID ? identifying.getUniqueId().toString() : identifying.getName();

    public AbstractBukkitSerializableManager(
            @NotNull CrudDatabaseCredentials credentials,
            @NotNull Function<LehmappCrudable, T> deserializer,
            @Nullable Function<T, S> joinEvent,
            @Nullable Function<T, S> quitEvent,
            @NotNull BlobPlugin plugin,
            @Nullable Supplier<Boolean> eventsRegistrationSupplier) {
        super(credentials, deserializer);
        Bukkit.getOnlinePlayers().forEach(player -> {
            String id = identifier.apply(player);
            BlobScheduler scheduler = plugin.getScheduler();
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
                BlobScheduler scheduler = plugin.getScheduler();
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
                plugin.getScheduler().async(() -> {
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

    public T lookFor(@NotNull Player player) {
        Objects.requireNonNull(player, "'player' cannot be null...");
        String id = identifier.apply(player);
        return cacheLook(id);
    }
}
