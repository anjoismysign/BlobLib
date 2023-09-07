package us.mytheria.bloblib.bungee;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.anjoismysign.anjo.entities.Uber;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.bson.Document;
import us.mytheria.bloblib.entities.DocumentDecorator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Manages instances that may be accessed by multiple servers.
 * It will cache instances in case a player attempts to connect to an
 * instance that is already running (maybe from a coop member) and
 * will synchronize these.
 * It also handles the instantiation of new instances and
 * will connect players to the instance with the lowest count.
 */
public class SharedSerializableInstanceManager implements Listener {
    private final Plugin blobBungeePlugin;
    private final String tag;
    private final int maxCount;
    private final BiMap<ServerInfo, Set<String>> cached;
    private final Map<ServerInfo, BungeeSharedSerializableInstance> gamemodeInstances;
    private final Map<String, CompletableFuture<Void>> joining;
    private final Map<String, String> pending;

    protected SharedSerializableInstanceManager(Plugin blobBungeePlugin,
                                                String pluginName,
                                                String crudableName,
                                                int maxCount) {
        this.blobBungeePlugin = blobBungeePlugin;
        this.tag = pluginName + "-" + crudableName;
        this.maxCount = maxCount;
        this.cached = HashBiMap.create();
        this.gamemodeInstances = new HashMap<>();
        this.joining = new HashMap<>();
        this.pending = new HashMap<>();
        this.blobBungeePlugin.getProxy().getServers().values().forEach(serverInfo -> {
            if (!serverInfo.getName().contains(pluginName))
                return;
            gamemodeInstances.put(serverInfo, new BungeeSharedSerializableInstance(maxCount));
        });
    }

    @EventHandler
    public void onMessage(PluginMessageEvent event) {
        if (!event.getTag().equals(tag))
            return;
        Document document = DocumentDecorator.deserialize(event.getData())
                .document();
        Connection connection = event.getSender();
        if (!(connection instanceof Server origin))
            return;
        String action = document.getString("Action").toLowerCase();
        switch (action) {
            case "shutdown" -> {
                ServerInfo info = origin.getInfo();
                gamemodeInstances.put(info, new BungeeSharedSerializableInstance(maxCount));
                cached.entrySet().removeIf(entry -> entry.getValue().equals(info));
            }
            case "quit" -> {
                String instanceId = document.getString("SharedSerializable#UniqueId");
                Set<String> instances = cached.values().stream()
                        .filter(set -> set.contains(instanceId))
                        .findFirst().orElse(null);
                if (instances == null)
                    return;
                ServerInfo info = cached.inverse().get(instances);
                if (info == null)
                    throw new NullPointerException("Instance not cached");
                if (pending.containsValue(instanceId)) {
                    /*
                     * Sends pendingJoin message to server
                     * This message is to alert fallback server
                     * that a player is being sent.
                     * TODO: Check algorithm in case when player is sent fails, to force
                     * TODO: ProxiedSharedSerializableManager closing the instance
                     * Listener: ProxiedSharedSerializableManager
                     */
                    Document send = new Document();
                    send.put("Action", "pendingJoin");
                    send.put("SharedSerializable#UniqueId", instanceId);
                    info.sendData(tag, new DocumentDecorator(send).serialize());
                    return;
                }
                //Sends instanceClosed message to server
                //Listener: ProxiedSharedSerializableManager
                Document send = new Document();
                send.put("Action", "instanceClosed");
                send.put("SharedSerializable#UniqueId", instanceId);
                info.sendData(tag, new DocumentDecorator(send).serialize());
                gamemodeInstances.get(info).decrement();
                cached.get(info).remove(instanceId);
            }
            case "joinconfirmed" -> {
                String uniqueName = document.getString("ProxiedPlayer#UniqueName");
                joining.get(uniqueName).complete(null);
                joining.remove(uniqueName);
                pending.remove(uniqueName);
            }
            case "instantiationconfirmed" -> {
                String uniqueName = document.getString("ProxiedPlayer#UniqueName");
                joining.get(uniqueName).complete(null);
                joining.remove(uniqueName);
                pending.remove(uniqueName);
                String instanceId = document.getString("SharedSerializable#UniqueId");
                //Let know cached that instanceId is now in origin
                ServerInfo info = origin.getInfo();
                Set<String> instances = cached.get(info);
                if (instances == null)
                    throw new NullPointerException("Instance not cached. Report BlobLib developers!");
                instances.add(instanceId);
            }
            case "hasinstance" -> {
                String uniqueName = document.getString("ProxiedPlayer#UniqueName");
                ProxiedPlayer player = blobBungeePlugin.getProxy().getPlayer(uniqueName);
                String instanceId = document.getString("SharedSerializable#UniqueId");
                Set<String> instances = cached.values().stream()
                        .filter(set -> set.contains(instanceId))
                        .findFirst().orElse(null);
                if (instances == null) {
                    ServerInfo info = findLowestCountInstance();
                    player.connect(info);
                    //Sends initialize message
                    //Listener: ProxiedSharedSerializableManager
                    Document send = new Document();
                    send.put("Action", "initialize");
                    send.put("SharedSerializable#OwnerName", player.getName());
                    send.put("SharedSerializable#UniqueId", instanceId);
                    info.sendData(tag, new DocumentDecorator(send).serialize());
                    /*
                     * Past 10 seconds, if future has not been completed (which means
                     * instantiation failed), then remove the player from the joining
                     * map and complete the future exceptionally.
                     * If successful, will increment the game mode instance count.
                     */
                    CompletableFuture<Void> future = new CompletableFuture<>();
                    future.thenRun(() -> gamemodeInstances.get(info).increment());
                    joining.put(player.getName(), future);
                    pending.put(player.getName(), instanceId);
                    blobBungeePlugin.getProxy().getScheduler().schedule(blobBungeePlugin, () -> {
                        if (!future.isDone()) {
                            joining.remove(player.getName());
                            pending.remove(player.getName());
                            future.completeExceptionally(new RuntimeException("Future did not complete within 10 seconds"));
                        }
                    }, 10, TimeUnit.SECONDS);
                    return;
                }
                ServerInfo info = cached.inverse().get(instances);
                if (player == null || info == null)
                    return;
                if (!player.isConnected())
                    return;
                info.ping((ping, throwable) -> {
                    if (throwable != null) {
                        //Sends connectionError message to origin
                        //Listener: GameModeInstanceLobby
                        Document send = new Document();
                        send.put("Action", "connectionError");
                        send.put("SharedSerializable#OwnerName", player.getName());
                        origin.sendData(tag, new DocumentDecorator(send).serialize());
                        return;
                    }
                    player.connect(info);
                    //Sends join message
                    //Listener: ProxiedSharedSerializableManager
                    CompletableFuture<Void> future = new CompletableFuture<>();
                    joining.put(player.getName(), CompletableFuture.completedFuture(null));
                    pending.put(player.getName(), instanceId);
                    blobBungeePlugin.getProxy().getScheduler().schedule(blobBungeePlugin, () -> {
                        if (!future.isDone()) {
                            joining.remove(player.getName());
                            pending.remove(player.getName());
                            future.completeExceptionally(new RuntimeException("Future did not complete within 10 seconds"));
                        }
                    }, 10, TimeUnit.SECONDS);
                    future.thenRun(() -> {
                        gamemodeInstances.get(info).increment();
                        joining.remove(player.getName());
                        pending.remove(player.getName());
                    });
                    Document send = new Document();
                    send.put("Action", "join");
                    send.put("SharedSerializable#OwnerName", player.getName());
                    send.put("SharedSerializable#UniqueId", instanceId);
                    info.sendData(tag, new DocumentDecorator(send).serialize());
                });
            }
            default -> {
            }
        }
    }

    private ServerInfo findLowestCountInstance() {
        Uber<ServerInfo> lowestCountInstance = Uber.fly();
        Uber<Integer> lowestCount = Uber.drive(Integer.MAX_VALUE);
        gamemodeInstances.forEach((serverInfo, instance) -> {
            if (instance.getCount() < lowestCount.thanks()) {
                lowestCountInstance.talk(serverInfo);
                lowestCount.talk(instance.getCount());
            }
        });
        return lowestCountInstance.thanks();
    }
}
