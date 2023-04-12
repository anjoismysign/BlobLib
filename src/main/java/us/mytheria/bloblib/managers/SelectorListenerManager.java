package us.mytheria.bloblib.managers;

import org.bukkit.entity.Player;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.listeners.EditorListener;
import us.mytheria.bloblib.entities.listeners.SelectorListener;

import javax.annotation.Nullable;
import java.util.HashMap;

public class SelectorListenerManager {
    private final BlobLib main;
    private final HashMap<String, SelectorListener<?>> selectorListener;
    private final HashMap<String, EditorListener<?>> editorListener;

    public SelectorListenerManager() {
        this.main = BlobLib.getInstance();
        this.selectorListener = new HashMap<>();
        this.editorListener = new HashMap<>();
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

    public void addEditorListener(Player player, EditorListener<?> listener) {
        String name = player.getName();
        if (editorListener.containsKey(name)) {
            main.getMessageManager().playAndSend(player, "System.Already-Editor-Listening");
            return;
        }
        listener.runTasks();
        editorListener.put(player.getName(), listener);
    }

    public void removeSelectorListener(Player player) {
        removeSelectorListener(player.getName());
    }

    public void removeEditorListener(Player player) {
        removeEditorListener(player.getName());
    }

    public void removeSelectorListener(String string) {
        selectorListener.remove(string);
    }

    public void removeEditorListener(String string) {
        editorListener.remove(string);
    }

    public void cancelSelectorListener(String string) {
        SelectorListener<?> selectorListener = this.selectorListener.get(string);
        if (selectorListener != null) {
            selectorListener.cancel();
            removeSelectorListener(string);
        }
    }

    public void cancelEditorListener(String string) {
        EditorListener<?> editorListener = this.editorListener.get(string);
        if (editorListener != null) {
            editorListener.cancel();
            removeEditorListener(string);
        }
    }

    public void cancelSelectorListener(Player player) {
        cancelSelectorListener(player.getName());
    }

    public void cancelEditorListener(Player player) {
        cancelEditorListener(player.getName());
    }

    @Nullable
    public Object getInput(Player player) {
        return selectorListener.get(player.getName()).getInput();
    }

    @Nullable
    public SelectorListener<?> getSelectorListener(Player player) {
        return getSelectorListener(player.getName());
    }

    @Nullable
    public SelectorListener<?> getSelectorListener(String string) {
        return selectorListener.get(string);
    }

    @Nullable
    public EditorListener<?> getEditorListener(Player player) {
        return getEditorListener(player.getName());
    }

    @Nullable
    public EditorListener<?> getEditorListener(String string) {
        return editorListener.get(string);
    }
}
