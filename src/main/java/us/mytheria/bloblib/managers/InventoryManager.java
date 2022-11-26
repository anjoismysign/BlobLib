package us.mytheria.bloblib.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.inventory.VariableSelector;
import us.mytheria.bloblib.entities.listeners.SelectorListener;

import java.util.HashMap;

public class InventoryManager implements Listener {
    private BlobLib main;
    private HashMap<String, VariableSelector> variableSelectors;

    public InventoryManager() {
        this.main = BlobLib.getInstance();
        Bukkit.getPluginManager().registerEvents(this, BlobLib.getInstance());
        this.variableSelectors = new HashMap<>();
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        if (!variableSelectors.containsKey(title))
            return;
        Player player = (Player) e.getWhoClicked();
        SelectorListener listener = main.getSelectorManager().get(player);
        if (listener == null)
            return;
        e.setCancelled(true);
        VariableSelector variableSelector = variableSelectors.get(title);
        int slot = e.getRawSlot();
        if (slot > variableSelector.valuesSize() - 1)
            return;
        Object value = variableSelector.getValue(slot);
        listener.setInput(value);
        player.closeInventory();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        String title = e.getView().getTitle();
        if (!variableSelectors.containsKey(title))
            return;
        Player player = (Player) e.getPlayer();
        SelectorListener listener = main.getSelectorManager().get(player);
        if (listener == null)
            return;
        listener.setInput(null);
    }
}
