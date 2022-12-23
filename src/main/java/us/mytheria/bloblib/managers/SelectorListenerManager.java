package us.mytheria.bloblib.managers;

import org.bukkit.entity.Player;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.listeners.SelectorListener;

import javax.annotation.Nullable;
import java.util.HashMap;

public class SelectorListenerManager {
    private final BlobLib main;
    private final HashMap<String, SelectorListener<?>> selectorListener;

    public SelectorListenerManager() {
        this.main = BlobLib.getInstance();
        this.selectorListener = new HashMap<>();
    }

    public void addSelectorListener(Player player, SelectorListener<?> listener) {
        String name = player.getName();
        if (selectorListener.containsKey(name)) {
            main.getMessageManager().playAndSend(player, "System.Already-Selector-Listening");
            return;
        }
        listener.runTasks();
        selectorListener.put(player.getName(), listener);
    }

    public void removeSelectorListener(Player player) {
        removeSelectorListener(player.getName());
    }

    public void removeSelectorListener(String string) {
        selectorListener.remove(string);
    }

    public void cancelSelectorListener(String string) {
        SelectorListener<?> selectorListener = this.selectorListener.get(string);
        if (selectorListener != null) {
            selectorListener.cancel();
            removeSelectorListener(string);
        }
    }

    public void cancelSelectorListener(Player player) {
        cancelSelectorListener(player.getName());
    }

    @Nullable
    public Object getInput(Player player) {
        return selectorListener.get(player.getName()).getInput();
    }

    @Nullable
    public SelectorListener<?> get(Player player) {
        return get(player.getName());
    }

    @Nullable
    public SelectorListener<?> get(String string) {
        return selectorListener.get(string);
    }
}
