package us.mytheria.bloblib.entities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.entities.inventory.BlobInventory;
import us.mytheria.bloblib.entities.inventory.BlobObjectBuilder;
import us.mytheria.bloblib.managers.ChatListenerManager;
import us.mytheria.bloblib.managers.DropListenerManager;
import us.mytheria.bloblib.managers.Manager;
import us.mytheria.bloblib.managers.ManagerDirector;
import us.mytheria.bloblib.managers.SelPosListenerManager;
import us.mytheria.bloblib.managers.SelectorListenerManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public abstract class BuilderManager<T extends BlobObject, B extends BlobObjectBuilder<T>> extends Manager implements Listener {
    private final ChatListenerManager chatManager;
    private final DropListenerManager dropListenerManager;
    private final SelectorListenerManager selectorListenerManager;
    private final SelPosListenerManager selPosListenerManager;
    private final String key;
    protected String title;
    protected Map<UUID, B> builders;
    protected Map<Inventory, B> inventories;

    public BuilderManager(ManagerDirector managerDirector,
                          String fileKey) {
        super(managerDirector);
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
        selPosListenerManager = getManagerDirector().getPositionListenerManager();
        chatManager = getManagerDirector().getChatListenerManager();
        dropListenerManager = getManagerDirector().getDropListenerManager();
        selectorListenerManager = getManagerDirector().getSelectorManager();
        this.key = Objects.requireNonNull(fileKey, "File key cannot be null.");
        update();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeBuilder(event.getPlayer());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory clicked = event.getClickedInventory();
        if (clicked == null)
            return;
        if (clicked.getType() == InventoryType.PLAYER)
            return;
        if (!inventories.containsKey(clicked))
            return;
        int slot = event.getRawSlot();
        Player player = (Player) event.getWhoClicked();
        BlobObjectBuilder<T> builder = getOrDefault(player.getUniqueId());
        if (slot >= builder.getSize()) {
            return;
        }
        event.setCancelled(true);
        builder.handle(slot, player);
    }

    @Override
    public void reload() {
        update();
    }

    public void update() {
        this.builders = new HashMap<>();
        this.inventories = new HashMap<>();
        BlobInventory inventory = BlobInventory.ofKeyOrThrow(key, (String) null);
        this.title = inventory.getTitle();
    }

    @NotNull
    public abstract B getOrDefault(UUID uuid);

    @NotNull
    public B getOrDefault(Player player) {
        return getOrDefault(player.getUniqueId());
    }

    @NotNull
    public BuilderManager<T, B> addBuilder(UUID uuid, B builder) {
        builders.put(uuid, builder);
        inventories.put(builder.getInventory(), builder);
        return this;
    }

    @NotNull
    public BuilderManager<T, B> addBuilder(Player player, B builder) {
        addBuilder(player.getUniqueId(), builder);
        return this;
    }

    @NotNull
    public BuilderManager<T, B> removeBuilder(UUID uuid) {
        B builder = builders.get(uuid);
        if (builder == null)
            return this;
        Inventory key = builder.getInventory();
        inventories.remove(key);
        builders.remove(uuid);
        return this;
    }

    @NotNull
    public BuilderManager<T, B> removeBuilder(Player player) {
        removeBuilder(player.getUniqueId());
        return this;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @NotNull
    public DropListenerManager getDropListenerManager() {
        return dropListenerManager;
    }

    @NotNull
    public ChatListenerManager getChatManager() {
        return chatManager;
    }

    @NotNull
    public SelectorListenerManager getSelectorListenerManager() {
        return selectorListenerManager;
    }

    @NotNull
    public SelPosListenerManager getSelPosListenerManager() {
        return selPosListenerManager;
    }
}
