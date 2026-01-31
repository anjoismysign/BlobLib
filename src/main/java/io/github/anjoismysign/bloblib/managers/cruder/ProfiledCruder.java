package io.github.anjoismysign.bloblib.managers.cruder;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.anjoismysign.bloblib.api.BlobLibProfileAPI;
import io.github.anjoismysign.bloblib.entities.PlayerDecorator;
import io.github.anjoismysign.bloblib.events.ProfileLoadEvent;
import io.github.anjoismysign.bloblib.middleman.profile.Profile;
import io.github.anjoismysign.psa.crud.Crudable;
import io.papermc.paper.connection.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public abstract class ProfiledCruder<T extends Crudable> implements Listener {

    final @NotNull JavaPlugin plugin;

    final @NotNull Map<UUID, BukkitTask> autoSave = Maps.newConcurrentMap();
    final @NotNull Set<UUID> loading = Sets.newConcurrentHashSet();
    final @NotNull Set<UUID> saving = Sets.newConcurrentHashSet();
    final @NotNull Map<String, PlayerDecorator> playerDecorators = Maps.newConcurrentMap();

    public ProfiledCruder(@NotNull JavaPlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPrejoin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        if (saving.contains(uuid)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You are already saving your data, please try again in a few seconds.");
        }
    }

    @EventHandler
    public void onLoad(ProfileLoadEvent event) {
        Profile profile = event.getProfile();
        Player player = event.getPlayer();
        var connection = player.getConnection();
        UUID uniqueId = player.getUniqueId();
        loading.add(uniqueId);
        playerDecorators.put(uniqueId.toString(), PlayerDecorator.of(player));
        Runnable asyncRunnable = loadRunnable(connection, uniqueId, profile);
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, asyncRunnable);
        } else {
            asyncRunnable.run();
        }
    }

    @NotNull PlayerDecorator assignPlayerDecorator(@NotNull String identification) {
        PlayerDecorator decorator = Objects.requireNonNull(playerDecorators.get(identification), "'" + identification + "' doesn't point to a PlayerDecorator");
        playerDecorators.remove(identification);
        return decorator;
    }

    abstract @Nullable T getAccount(@NotNull UUID uniqueIdentifier);

    abstract Runnable loadRunnable(PlayerConnection connection,
                                   UUID uniqueId,
                                   Profile profile);

    void loadAll() {
        var scheduler = Bukkit.getScheduler();
        Bukkit.getOnlinePlayers().forEach(player -> {
            var connection = player.getConnection();
            scheduler.runTaskTimer(plugin, task -> {
                if (!connection.isConnected()){
                    task.cancel();
                    return;
                }
                var profileManagement = BlobLibProfileAPI.getInstance().getProvider().getProfileManagement(player);
                var currentProfileIndex = profileManagement.getCurrentProfileIndex();
                if (currentProfileIndex < 0){
                    return;
                }
                var profiles = profileManagement.getProfiles();
                var profile = profiles.get(currentProfileIndex);
                UUID uniqueId = player.getUniqueId();
                loading.add(uniqueId);
                playerDecorators.put(uniqueId.toString(), PlayerDecorator.of(player));
                Runnable asyncRunnable = loadRunnable(connection, uniqueId, profile);
                scheduler.runTaskAsynchronously(plugin, asyncRunnable);
                task.cancel();
            }, 0, 1);
        });
    }

    abstract public void shutdown();

    @Nullable
    public T getAccount(@NotNull Player player) {
        return getAccount(player.getUniqueId());
    }

    @NotNull
    public List<T> getAccounts(){
        return Bukkit.getOnlinePlayers()
                .stream()
                .map(player -> getAccount(player.getUniqueId()))
                .filter(Objects::nonNull)
                .toList();
    }

}
