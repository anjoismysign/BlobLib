package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.api.BlobLibInventoryAPI;
import io.github.anjoismysign.bloblib.api.BlobLibSoundAPI;
import io.github.anjoismysign.bloblib.entities.BlobEditor;
import io.github.anjoismysign.bloblib.entities.inventory.ClickEventProcessor;
import io.github.anjoismysign.bloblib.entities.inventory.InventoryDataRegistry;
import io.github.anjoismysign.bloblib.entities.inventory.VariableSelector;
import io.github.anjoismysign.bloblib.entities.listeners.EditorListener;
import io.github.anjoismysign.bloblib.entities.listeners.SelectorListener;
import io.github.anjoismysign.bloblib.entities.message.BlobSound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Objects;

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
        String reference = Objects.requireNonNull(blobEditor.getKey(), "'blobEditor' was dynamically constructed by BlobLib without providing a reference");
        InventoryDataRegistry<?> registry = BlobLibInventoryAPI.getInstance().getInventoryDataRegistry(reference);
        BlobSound clickSound = BlobLibSoundAPI.getInstance().getSound("Builder.Button-Click");
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
        if (!listener.setInputFromSlot(blobEditor, event.getRawSlot())) {
            blobEditor.getKeys().forEach(key -> {
                var button = blobEditor.getButton(key);
                if (!button.containsSlot(slot))
                    return;
                if (!button.handleAll((Player) event.getWhoClicked()))
                    return;
                registry.processSingleClickEvent(key, event);
                button.accept(ClickEventProcessor.of(event, registry));
            });
            return;
        }
        clickSound.handle(player);
    }

    @EventHandler
    public void onSelectorClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        VariableSelector<?> variableSelector = variableSelectors.get(player.getName());
        if (variableSelector == null)
            return;
        SelectorListener<?> listener = main.getSelectorManager().getSelectorListener(player);
        if (listener == null)
            return;
        event.setCancelled(true);
        String reference = Objects.requireNonNull(variableSelector.getKey(), "'variableSelector' was dynamically constructed by BlobLib without providing a reference");
        InventoryDataRegistry<?> registry = BlobLibInventoryAPI.getInstance().getInventoryDataRegistry(reference);
        int slot = event.getRawSlot();
        BlobSound clickSound = BlobLibSoundAPI.getInstance().getSound("Builder.Button-Click");
        if (variableSelector.isNextPageButton(slot)) {
            variableSelector.nextPage();
            clickSound.handle(player);
            return;
        }
        if (variableSelector.isPreviousPageButton(slot)) {
            variableSelector.previousPage();
            clickSound.handle(player);
            return;
        }
        if (variableSelector.isReturnButton(slot)) {
            variableSelector.processReturn();
            clickSound.handle(player);
            return;
        }
        if (!listener.setInputFromSlot(variableSelector, slot)) {
            variableSelector.getKeys().forEach(key -> {
                var button = variableSelector.getButton(key);
                if (!button.containsSlot(slot))
                    return;
                if (!button.handleAll((Player) event.getWhoClicked()))
                    return;
                registry.processSingleClickEvent(key, event);
                button.accept(ClickEventProcessor.of(event, registry));
            });
            return;
        }
        listener.getClickSound().handle(player);
    }

    @EventHandler
    public void onSelectorClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (!variableSelectors.containsKey(player.getName()))
            return;
        SelectorListener<?> listener = main.getSelectorManager().getSelectorListener(player);
        if (listener == null)
            return;
        listener.setInput(null);
    }

    @EventHandler
    public void onEditorClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
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
