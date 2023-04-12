package us.mytheria.bloblib.managers;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.BlobEditor;
import us.mytheria.bloblib.entities.inventory.VariableSelector;
import us.mytheria.bloblib.entities.listeners.EditorListener;
import us.mytheria.bloblib.entities.listeners.SelectorListener;

import java.util.HashMap;

public class VariableSelectorManager implements Listener {
    private final BlobLib main;
    private final HashMap<String, VariableSelector<?>> variableSelectors;
    private final HashMap<String, BlobEditor<?>> blobEditors;

    public VariableSelectorManager() {
        this.main = BlobLib.getInstance();
        Bukkit.getPluginManager().registerEvents(this, BlobLib.getInstance());
        this.variableSelectors = new HashMap<>();
        this.blobEditors = new HashMap<>();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (!variableSelectors.containsKey(player.getName())) {
            if (!blobEditors.containsKey(player.getName()))
                return;
            EditorListener<?> listener = main.getSelectorManager().getEditorListener(player);
            if (listener == null)
                return;
            e.setCancelled(true);
            BlobEditor<?> blobEditor = blobEditors.get(player.getName());
            listener.setInputFromSlot(blobEditor, e.getRawSlot());
            return;
        }
        SelectorListener<?> listener = main.getSelectorManager().getSelectorListener(player);
        if (listener == null)
            return;
        e.setCancelled(true);
        VariableSelector<?> variableSelector = variableSelectors.get(player.getName());
        int slot = e.getRawSlot();
        if (slot > variableSelector.valuesSize() - 1) {
            if (variableSelector.isNextPageButton(slot)) {
                variableSelector.nextPage();
                return;
            }
            if (variableSelector.isPreviousPageButton(slot)) {
                variableSelector.previousPage();
            }
            return;
        }
        listener.setInputFromSlot(variableSelector, slot);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        if (!variableSelectors.containsKey(player.getName()))
            return;
        SelectorListener<?> listener = main.getSelectorManager().getSelectorListener(player);
        if (listener == null)
            return;
        listener.setInput(null);
    }

    public void addVariableSelector(VariableSelector<?> variableSelector) {
        variableSelectors.put(variableSelector.getPlayer().getName(), variableSelector);
    }

    public void addEditorSelector(BlobEditor<?> blobEditor) {
        blobEditors.put(blobEditor.getPlayer().getName(), blobEditor);
    }
}
