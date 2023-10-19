package us.mytheria.bloblib.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.api.BlobLibMessageAPI;
import us.mytheria.bloblib.entities.listeners.DropListener;

import java.util.HashMap;

public class DropListenerManager implements Listener {
    private BlobLib main;
    private HashMap<String, DropListener> dropListeners;

    public DropListenerManager() {
        this.main = BlobLib.getInstance();
        Bukkit.getPluginManager().registerEvents(this, BlobLib.getInstance());
        this.dropListeners = new HashMap<>();
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        String name = e.getPlayer().getName();
        DropListener listener = dropListeners.get(name);
        if (listener == null)
            return;
        listener.setInput(e.getItemDrop().getItemStack());
        e.setCancelled(true);
    }

    public void addDropListener(Player player, DropListener listener) {
        String name = player.getName();
        if (dropListeners.containsKey(name)) {
            BlobLibMessageAPI.getInstance().getMessage("System.Already-Drop-Listening", player)
                    .handle(player);
            return;
        }
        listener.runTasks();
        dropListeners.put(player.getName(), listener);
    }

    public void removeDropListener(Player player) {
        removeDropListener(player.getName());
    }

    public void removeDropListener(String string) {
        dropListeners.remove(string);
    }

    public void cancelDropListener(String string) {
        DropListener dropListener = this.dropListeners.get(string);
        if (dropListener != null) {
            dropListener.cancel();
            removeDropListener(string);
        }
    }

    public void cancelDropListener(Player player) {
        cancelDropListener(player.getName());
    }

    @Nullable
    public ItemStack getInput(Player player) {
        return dropListeners.get(player.getName()).getInput();
    }

    @Nullable
    public DropListener get(Player player) {
        return get(player.getName());
    }

    @Nullable
    public DropListener get(String string) {
        return dropListeners.get(string);
    }
}
