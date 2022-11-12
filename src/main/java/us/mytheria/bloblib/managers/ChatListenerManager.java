package us.mytheria.bloblib.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.chatlistener.ChatListener;

import javax.annotation.Nullable;
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
        if (!chatListeners.containsKey(e.getPlayer().getName()))
            return;
        ChatListener listener = chatListeners.get(e.getPlayer().getName());
        listener.setInput(e.getMessage());
    }

    public void addChatListener(Player player, ChatListener chatListener) {
        String name = player.getName();
        if (chatListeners.containsKey(name)) {
            player.sendMessage(main.getLangManager().getLang("msg.Already-Chat-Listening"));
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
