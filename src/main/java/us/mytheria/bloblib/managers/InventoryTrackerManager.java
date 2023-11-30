package us.mytheria.bloblib.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;
import us.mytheria.bloblib.entities.inventory.*;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.WeakHashMap;

public class InventoryTrackerManager implements Listener {
    private final BlobLib plugin;
    private final WeakHashMap<Inventory, InventoryTracker<?, ?>> tracker;
    private final HashMap<UUID, InventoryTracker<?, ?>> playerTracker;

    public InventoryTrackerManager() {
        plugin = BlobLib.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.tracker = new WeakHashMap<>();
        this.playerTracker = new HashMap<>();
    }

    /**
     * Will track the inventory of the specified player.
     *
     * @param player the player
     * @param key    the key of the inventory
     * @return the BlobInventoryTracker, null if the key is not found
     */
    @Nullable
    public BlobInventoryTracker trackInventory(@NotNull Player player, @NotNull String key) {
        Objects.requireNonNull(player, "player cannot be null");
        String locale = player.getLocale();
        locale = BlobLibTranslatableAPI.getInstance().getRealLocale(locale);
        InventoryDataRegistry<InventoryButton> registry = plugin.getInventoryManager().getInventoryDataRegistry(key);
        if (registry == null)
            return null;
        BlobInventory inventory = BlobInventory.fromInventoryBuilderCarrier(registry.get(locale));
        BlobInventoryTracker tracker = BlobInventoryTracker.of(inventory, locale, registry);
        this.tracker.put(inventory.getInventory(), tracker);
        this.playerTracker.put(player.getUniqueId(), tracker);
        return tracker;
    }

    /**
     * Will track the inventory of the specified player.
     *
     * @param player the player
     * @param key    the key of the inventory
     * @return the MetaBlobInventoryTracker, null if the key is not found
     */
    @Nullable
    public MetaBlobInventoryTracker trackMetaInventory(@NotNull Player player, @NotNull String key) {
        Objects.requireNonNull(player, "player cannot be null");
        String locale = player.getLocale();
        locale = BlobLibTranslatableAPI.getInstance().getRealLocale(locale);
        InventoryDataRegistry<MetaInventoryButton> registry = plugin.getInventoryManager()
                .getMetaInventoryDataRegistry(key);
        if (registry == null)
            return null;
        MetaBlobInventory inventory = MetaBlobInventory.fromInventoryBuilderCarrier(registry.get(locale));
        MetaBlobInventoryTracker tracker = MetaBlobInventoryTracker.of(inventory, locale, registry);
        this.tracker.put(inventory.getInventory(), tracker);
        this.playerTracker.put(player.getUniqueId(), tracker);
        return tracker;
    }

    @EventHandler
    private void onLocaleChange(PlayerLocaleChangeEvent event) {
        Player player = event.getPlayer();
        InventoryTracker<?, ?> result = this.playerTracker.get(player.getUniqueId());
        if (result == null)
            return;
        this.tracker.remove(result.getInventory().getInventory());
        String locale = event.getLocale();
        result.setLocale(locale);
        this.tracker.put(result.getInventory().getInventory(), result);
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null)
            return;
        InventoryTracker<?, ?> inventoryTracker = this.tracker.get(clickedInventory);
        if (inventoryTracker == null)
            return;
        SharableInventory<?> sharableInventory = inventoryTracker.getInventory();
        InventoryDataRegistry<?> registry = inventoryTracker.getRegistry();
        int slot = event.getRawSlot();
        event.setCancelled(true);
        sharableInventory.getKeys().forEach(key -> {
            var button = sharableInventory.getButton(key);
            if (!button.containsSlot(slot))
                return;
            registry.processClickEvent(key, event);
        });
    }
}
