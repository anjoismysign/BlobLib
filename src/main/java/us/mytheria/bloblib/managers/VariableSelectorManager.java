package us.mytheria.bloblib.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.BlobEditor;
import us.mytheria.bloblib.entities.inventory.VariableSelector;
import us.mytheria.bloblib.entities.listeners.EditorListener;
import us.mytheria.bloblib.entities.listeners.SelectorListener;
import us.mytheria.bloblib.entities.message.BlobSound;

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
            if (!blobEditors.containsKey(player.getName())) {
                player.sendMessage("Doesn't contains BlobEditor");
                return;
            }
            EditorListener<?> listener = main.getSelectorManager().getEditorListener(player);
            if (listener == null)
                return;
            e.setCancelled(true);
            BlobEditor<?> blobEditor = blobEditors.get(player.getName());
            int slot = e.getRawSlot();
            BlobSound clickSound = BlobLibAssetAPI.getSound("Builder.Button-Click");
            if (slot > blobEditor.valuesSize() - 1) {
                if (blobEditor.isNextPageButton(slot)) {
                    blobEditor.nextPage();
                    return;
                }
                if (blobEditor.isPreviousPageButton(slot)) {
                    blobEditor.previousPage();
                    return;
                }
                if (blobEditor.isAddElementButton(slot)) {
                    clickSound.handle(player);
                    blobEditor.addElement(player);
                    return;
                }
                if (blobEditor.isRemoveElementButton(slot)) {
                    clickSound.handle(player);
                    blobEditor.removeElement(player);
                    return;
                }
                return;
            }
            listener.setInputFromSlot(blobEditor, e.getRawSlot());
            clickSound.handle(player);
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
        BlobLibAssetAPI.getSound("Builder.Button-Click").handle(player);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        if (!variableSelectors.containsKey(player.getName())) {
            if (!blobEditors.containsKey(player.getName()))
                return;
            EditorListener<?> listener = main.getSelectorManager().getEditorListener(player);
            if (listener == null)
                return;
            listener.setInput(null);
            player.sendMessage("§cYou have closed the editor without selecting a value.");
            return;
        }
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
