package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.api.BlobLibMessageAPI;
import io.github.anjoismysign.bloblib.entities.listeners.SelPosListener;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class SelPosListenerManager implements Listener {
    private BlobLib main;
    private HashMap<String, SelPosListener> positionListeners;

    public SelPosListenerManager() {
        this.main = BlobLib.getInstance();
        Bukkit.getPluginManager().registerEvents(this, BlobLib.getInstance());
        this.positionListeners = new HashMap<>();
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK)
            return;
        if (e.getHand() != EquipmentSlot.HAND)
            return;
        Player player = e.getPlayer();
        SelPosListener listener = positionListeners.get(player.getName());
        if (listener == null)
            return;
        listener.setInput(e.getClickedBlock());
        e.setCancelled(true);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (e.getHand() != EquipmentSlot.HAND)
            return;
        Player player = e.getPlayer();
        SelPosListener listener = positionListeners.get(player.getName());
        if (listener == null)
            return;
        listener.setInput(e.getClickedBlock());
        e.setCancelled(true);
    }

    public void addPositionListener(Player player, SelPosListener posListener) {
        String name = player.getName();
        if (positionListeners.containsKey(name)) {
            BlobLibMessageAPI.getInstance().getMessage("System.Already-SelPos-Listening", player)
                    .handle(player);
            return;
        }
        posListener.runTasks();
        positionListeners.put(player.getName(), posListener);
    }

    public void removePositionListener(Player player) {
        removePositionListener(player.getName());
    }

    public void removePositionListener(String string) {
        positionListeners.remove(string);
    }

    public void cancelPositionListener(String string) {
        SelPosListener positionListener = positionListeners.get(string);
        if (positionListener != null) {
            positionListener.cancel();
            removePositionListener(string);
        }
    }

    public void cancelPositionListener(Player player) {
        cancelPositionListener(player.getName());
    }

    @Nullable
    public Block getInput(Player player) {
        return positionListeners.get(player.getName()).getInput();
    }
}
