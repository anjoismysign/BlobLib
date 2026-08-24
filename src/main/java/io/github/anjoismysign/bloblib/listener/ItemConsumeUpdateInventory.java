package io.github.anjoismysign.bloblib.listener;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.manager.BlobLibConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemConsumeUpdateInventory implements Listener {
    private final BlobLibConfigManager configManager;

    public ItemConsumeUpdateInventory(BlobLibConfigManager configManager) {
        this.configManager = configManager;
    }

    public void reload() {
        HandlerList.unregisterAll(this);
        if (configManager.getItemConsumeUpdateInventory().register()) {
            Bukkit.getPluginManager().registerEvents(this, BlobLib.getInstance());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(PlayerInteractEvent event) {
        @Nullable ItemStack itemStack = event.getItem();
        if (itemStack == null){
            return;
        }
        Bukkit.getScheduler().runTaskLater(BlobLib.getInstance(), ()->{
            event.getPlayer().updateInventory();
        }, 3L);
    }
}
