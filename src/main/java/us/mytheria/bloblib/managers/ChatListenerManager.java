package us.mytheria.bloblib.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.api.BlobLibMessageAPI;
import us.mytheria.bloblib.entities.listeners.ChatListener;

import java.util.HashMap;

public class ChatListenerManager implements Listener {
    private BlobLib main;
    private HashMap<String, ChatListener> chatListeners;

    public ChatListenerManager() {
        this.main = BlobLib.getInstance();
        Bukkit.getPluginManager().registerEvents(this, BlobLib.getInstance());
        this.chatListeners = new HashMap<>();
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        ChatListener listener = chatListeners.get(e.getPlayer().getName());
        if (listener == null)
            return;
        listener.setInput(e.getMessage());
        e.setCancelled(true);
    }

    public void addChatListener(Player player, ChatListener chatListener) {
        String name = player.getName();
        if (chatListeners.containsKey(name)) {
            BlobLibMessageAPI.getInstance().getMessage("System.Already-Chat-Listening", player)
                    .handle(player);
            return;
        }
        chatListener.runTasks();
        chatListeners.put(player.getName(), chatListener);
    }

    public void removeChatListener(Player player) {
        removeChatListener(player.getName());
    }

    public void removeChatListener(String string) {
        chatListeners.remove(string);
    }

    public void cancelChatListener(String string) {
        ChatListener chatListener = chatListeners.get(string);
        if (chatListener != null) {
            chatListener.cancel();
            removeChatListener(string);
        }
    }

    public void cancelChatListener(Player player) {
        cancelChatListener(player.getName());
    }

    @Nullable
    public String getInput(Player player) {
        return chatListeners.get(player.getName()).getInput();
    }
}
