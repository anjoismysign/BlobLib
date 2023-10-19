package us.mytheria.bloblib.entities;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public interface BlobSerializableHandler extends Listener {
    JavaPlugin getPlugin();

    void onJoin(PlayerJoinEvent e);

    void onQuit(PlayerQuitEvent e);

    default void registerJoinListener(PluginManager pluginManager, EventPriority joinPriority) {
        switch (joinPriority) {
            case LOWEST -> pluginManager.registerEvents(new Listener() {
                @EventHandler(priority = EventPriority.LOWEST)
                public void onJoin(PlayerJoinEvent e) {
                    BlobSerializableHandler.this.onJoin(e);
                }
            }, getPlugin());
            case LOW -> pluginManager.registerEvents(new Listener() {
                @EventHandler(priority = EventPriority.LOW)
                public void onJoin(PlayerJoinEvent e) {
                    BlobSerializableHandler.this.onJoin(e);
                }
            }, getPlugin());
            case NORMAL -> pluginManager.registerEvents(new Listener() {
                @EventHandler(priority = EventPriority.NORMAL)
                public void onJoin(PlayerJoinEvent e) {
                    BlobSerializableHandler.this.onJoin(e);
                }
            }, getPlugin());
            case HIGH -> pluginManager.registerEvents(new Listener() {
                @EventHandler(priority = EventPriority.HIGH)
                public void onJoin(PlayerJoinEvent e) {
                    BlobSerializableHandler.this.onJoin(e);
                }
            }, getPlugin());
            case HIGHEST -> pluginManager.registerEvents(new Listener() {
                @EventHandler(priority = EventPriority.HIGHEST)
                public void onJoin(PlayerJoinEvent e) {
                    BlobSerializableHandler.this.onJoin(e);
                }
            }, getPlugin());
            case MONITOR -> pluginManager.registerEvents(new Listener() {
                @EventHandler(priority = EventPriority.MONITOR)
                public void onJoin(PlayerJoinEvent e) {
                    BlobSerializableHandler.this.onJoin(e);
                }
            }, getPlugin());
            default -> throw new IllegalStateException("Unexpected value: " + joinPriority);
        }
    }

    default void registerQuitListener(PluginManager pluginManager, EventPriority quitPriority) {
        switch (quitPriority) {
            case LOWEST -> pluginManager.registerEvents(new Listener() {
                @EventHandler(priority = EventPriority.LOWEST)
                public void onQuit(PlayerQuitEvent e) {
                    BlobSerializableHandler.this.onQuit(e);
                }
            }, getPlugin());
            case LOW -> pluginManager.registerEvents(new Listener() {
                @EventHandler(priority = EventPriority.LOW)
                public void onQuit(PlayerQuitEvent e) {
                    BlobSerializableHandler.this.onQuit(e);
                }
            }, getPlugin());
            case NORMAL -> pluginManager.registerEvents(new Listener() {
                @EventHandler(priority = EventPriority.NORMAL)
                public void onQuit(PlayerQuitEvent e) {
                    BlobSerializableHandler.this.onQuit(e);
                }
            }, getPlugin());
            case HIGH -> pluginManager.registerEvents(new Listener() {
                @EventHandler(priority = EventPriority.HIGH)
                public void onQuit(PlayerQuitEvent e) {
                    BlobSerializableHandler.this.onQuit(e);
                }
            }, getPlugin());
            case HIGHEST -> pluginManager.registerEvents(new Listener() {
                @EventHandler(priority = EventPriority.HIGHEST)
                public void onQuit(PlayerQuitEvent e) {
                    BlobSerializableHandler.this.onQuit(e);
                }
            }, getPlugin());
            case MONITOR -> pluginManager.registerEvents(new Listener() {
                @EventHandler(priority = EventPriority.MONITOR)
                public void onQuit(PlayerQuitEvent e) {
                    BlobSerializableHandler.this.onQuit(e);
                }
            }, getPlugin());
            default -> throw new IllegalStateException("Unexpected value: " + quitPriority);
        }
    }
}
