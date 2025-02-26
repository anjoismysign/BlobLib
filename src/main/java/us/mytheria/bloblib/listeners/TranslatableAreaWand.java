package us.mytheria.bloblib.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.api.BlobLibMessageAPI;
import us.mytheria.bloblib.entities.translatable.TranslatableItem;

import java.util.HashMap;
import java.util.Map;

public class TranslatableAreaWand implements Listener {

    private static final Map<String, Wand> wands = new HashMap<>();

    public static final class Wand {
        private @Nullable Block min;
        private @Nullable Block max;

        public Wand(@Nullable Block min,
                    @Nullable Block max) {
            this.min = min;
            this.max = max;
        }

        @Nullable
        public BoundingBox hasBoundingBox() {
            if (min == null || max == null)
                return null;
            if (!min.getWorld().getName().equals(max.getWorld().getName()))
                return null;
            return BoundingBox.of(min, max);
        }

    }

    public TranslatableAreaWand() {
        Bukkit.getPluginManager().registerEvents(this, BlobLib.getInstance());
    }

    @Nullable
    public BoundingBox has(@NotNull Player player) {
        String name = player.getName();
        @Nullable Wand wand = wands.get(name);
        if (wand == null)
            return null;
        return wand.hasBoundingBox();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND)
            return;
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        @Nullable TranslatableItem translatableItem = TranslatableItem.byItemStack(itemStack);
        if (translatableItem == null)
            return;
        if (!translatableItem.getReference().equals("TranslatableArea.Wand"))
            return;
        @Nullable Block clicked = event.getClickedBlock();
        if (clicked == null)
            return;
        boolean isMin = event.getAction().isLeftClick();
        String name = player.getName();
        Wand wand = wands.computeIfAbsent(name, k -> new Wand(null, null));
        event.setCancelled(true);
        BlobLibMessageAPI messageAPI = BlobLibMessageAPI.getInstance();
        if (isMin) {
            wand.min = clicked;
            messageAPI.getMessage("TranslatableArea.Wand-Min", player)
                    .modder()
                    .replace("%pos%", toString(clicked))
                    .get()
                    .handle(player);
        } else {
            wand.max = clicked;
            messageAPI.getMessage("TranslatableArea.Wand-Max", player)
                    .modder()
                    .replace("%pos%", toString(clicked))
                    .get()
                    .handle(player);
        }
    }

    private final String toString(Block block) {
        Vector vector = block.getLocation().toVector();
        return vector.getBlockX() + "," + vector.getBlockY() + "," + vector.getBlockZ();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        wands.remove(name);
    }

}
