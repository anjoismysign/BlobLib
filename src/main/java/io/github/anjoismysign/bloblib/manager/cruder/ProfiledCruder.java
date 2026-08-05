package io.github.anjoismysign.bloblib.manager.cruder;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.anjoismysign.bloblib.api.BlobLibProfileAPI;
import io.github.anjoismysign.bloblib.domain.PlayerDecorator;
import io.github.anjoismysign.bloblib.profile.ProfileView;
import io.github.anjoismysign.psa.crud.Crudable;
import io.papermc.paper.connection.PlayerConnection;
import net.milkbowl.vault.profile.ProfileLoadEvent;
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

    final long autoSaveIntervalTicks = 20 * 60 * 5;
    final long autoSaveIntervalJitter;

    public ProfiledCruder(@NotNull JavaPlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        autoSaveIntervalJitter = Math.abs(plugin.getName().hashCode() % 200L);
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
        ProfileView profile = ProfileView.of(event.getIdentification(), event.getName());
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
                                   ProfileView profile);

    void loadAll() {
        var scheduler = Bukkit.getScheduler();
        Bukkit.getOnlinePlayers().forEach(player -> {
            var connection = player.getConnection();
            scheduler.runTaskTimer(plugin, task -> {
                if (!connection.isConnected()){
                    task.cancel();
                    return;
                }
                var elasticProfile = BlobLibProfileAPI.getInstance().getProvider();
                var currentProfileIndex = elasticProfile.getCurrentProfileIndex(player);
                if (currentProfileIndex < 0){
                    return;
                }
                var profile = elasticProfile.toView(player, currentProfileIndex);
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
