package us.mytheria.bloblib.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.api.BlobLibSoundAPI;
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
    public void onEditorClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        BlobEditor<?> blobEditor = blobEditors.get(player.getName());
        if (blobEditor == null) {
            return;
        }
        EditorListener<?> listener = main.getSelectorManager().getEditorListener(player);
        if (listener == null) {
            return;
        }
        event.setCancelled(true);
        int slot = event.getRawSlot();
        BlobSound clickSound = BlobLibSoundAPI.getInstance().getSound("Builder.Button-Click");
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
            if (blobEditor.isReturnButton(slot)) {
                blobEditor.processReturn();
                return;
            }
            return;
        }
        listener.setInputFromSlot(blobEditor, event.getRawSlot());
        clickSound.handle(player);
    }

    @EventHandler
    public void onSelectorClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        VariableSelector<?> variableSelector = variableSelectors.get(player.getName());
        if (variableSelector == null)
            return;
        SelectorListener<?> listener = main.getSelectorManager().getSelectorListener(player);
        if (listener == null)
            return;
        e.setCancelled(true);
        int slot = e.getRawSlot();
        if (slot > variableSelector.valuesSize() - 1) {
            if (variableSelector.isNextPageButton(slot)) {
                variableSelector.nextPage();
                return;
            }
            if (variableSelector.isPreviousPageButton(slot)) {
                variableSelector.previousPage();
                return;
            }
            if (variableSelector.isReturnButton(slot)) {
                variableSelector.processReturn();
                return;
            }
            return;
        }
        listener.setInputFromSlot(variableSelector, slot);
        BlobLibSoundAPI.getInstance().getSound("Builder.Button-Click").handle(player);
    }

    @EventHandler
    public void onSelectorClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        if (!variableSelectors.containsKey(player.getName()))
            return;
        SelectorListener<?> listener = main.getSelectorManager().getSelectorListener(player);
        if (listener == null)
            return;
        listener.setInput(null);
    }

    @EventHandler
    public void onEditorClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        if (!blobEditors.containsKey(player.getName()))
            return;
        EditorListener<?> listener = main.getSelectorManager().getEditorListener(player);
        if (listener == null)
            return;
        listener.setInput(null);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeMapping(event.getPlayer());
    }

    /**
     * Adds a new VariableSelector to the mapping
     *
     * @param variableSelector The VariableSelector
     */
    public void addVariableSelector(VariableSelector<?> variableSelector) {
        variableSelectors.put(variableSelector.getPlayer().getName(), variableSelector);
    }

    /**
     * Adds a new BlobEditor to the mapping
     *
     * @param blobEditor The BlobEditor
     */
    public void addEditorSelector(BlobEditor<?> blobEditor) {
        blobEditors.put(blobEditor.getPlayer().getName(), blobEditor);
    }

    /**
     * Removes the mapping of the player
     *
     * @param player The player
     */
    public void removeMapping(Player player) {
        variableSelectors.remove(player.getName());
        blobEditors.remove(player.getName());
    }
}
