package io.github.anjoismysign.bloblib.listeners;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.managers.BlobLibConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;

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
    public void handle(VehicleExitEvent event) {
        if (!event.getExited().getType().toString().contains("DISPLAY"))
            return;
        event.setCancelled(true);
    }
}
