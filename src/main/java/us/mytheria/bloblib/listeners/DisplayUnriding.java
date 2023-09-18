package us.mytheria.bloblib.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.managers.BlobLibConfigManager;

public class DisplayUnriding implements Listener {
    private final BlobLibConfigManager configManager;

    public DisplayUnriding(BlobLibConfigManager configManager) {
        this.configManager = configManager;
    }

    public void reload() {
        HandlerList.unregisterAll(this);
        if (configManager.getDisplayRiding().register()) {
            Bukkit.getPluginManager().registerEvents(this, BlobLib.getInstance());
        }
    }

    @EventHandler
    public void handle(VehicleExitEvent e) {
        if (!e.getExited().getType().toString().contains("DISPLAY"))
            return;
        e.setCancelled(true);
    }
}
