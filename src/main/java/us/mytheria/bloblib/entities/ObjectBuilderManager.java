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
import us.mytheria.bloblib.api.BlobLibInventoryAPI;
import us.mytheria.bloblib.entities.inventory.BlobInventory;
import us.mytheria.bloblib.entities.inventory.ObjectBuilder;
import us.mytheria.bloblib.managers.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;

public class ObjectBuilderManager<T extends BlobObject> extends Manager implements Listener {
    protected String title;

    private Map<UUID, ObjectBuilder<T>> builders;
    private Map<Inventory, ObjectBuilder<T>> inventories;
    private ChatListenerManager chatManager;
    private DropListenerManager dropListenerManager;
    private SelectorListenerManager selectorListenerManager;
    private SelPosListenerManager selPosListenerManager;
    private final String fileKey;
    private BiFunction<UUID, ObjectDirector<T>, ObjectBuilder<T>> builderBiFunction;
    private final ObjectDirector<T> objectDirector;

    public ObjectBuilderManager(ManagerDirector managerDirector,
                                String fileKey, ObjectDirector<T> objectDirector) {
        super(managerDirector);
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
        selPosListenerManager = getManagerDirector().getPositionListenerManager();
        chatManager = getManagerDirector().getChatListenerManager();
        dropListenerManager = getManagerDirector().getDropListenerManager();
        selectorListenerManager = getManagerDirector().getSelectorManager();
        this.objectDirector = Objects.requireNonNull(objectDirector, "Object director cannot be null.");
        this.fileKey = Objects.requireNonNull(fileKey, "File key cannot be null.");
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
        ObjectBuilder<T> builder = getOrDefault(player.getUniqueId());
        if (slot >= builder.getSize()) {
            return;
        }
        event.setCancelled(true);
        builder.handle(slot, player);
    }

    @NotNull
    public ObjectDirector<T> getObjectDirector() {
        return objectDirector;
    }

    @Override
    public void reload() {
        update();
    }

    public void update() {
        this.builders = new HashMap<>();
        this.inventories = new HashMap<>();
        BlobInventory inventory = BlobLibInventoryAPI.getInstance().getBlobInventory(fileKey);
        if (inventory == null)
            throw new RuntimeException("Inventory file '" + fileKey + "' not found.");
        this.title = inventory.getTitle();
    }

    @NotNull
    public ObjectBuilder<T> getOrDefault(UUID uuid) {
        ObjectBuilder<T> objectBuilder = builders.get(uuid);
        if (objectBuilder == null) {
            objectBuilder = builderBiFunction.apply(uuid, getObjectDirector());
            builders.put(uuid, objectBuilder);
            inventories.put(objectBuilder.getInventory(), objectBuilder);
        }
        return objectBuilder;
    }

    @NotNull
    public ObjectBuilder<T> getOrDefault(Player player) {
        return getOrDefault(player.getUniqueId());
    }

    @NotNull
    public ObjectBuilderManager<T> addBuilder(UUID uuid, ObjectBuilder<T> builder) {
        builders.put(uuid, builder);
        inventories.put(builder.getInventory(), builder);
        return this;
    }

    @NotNull
    public ObjectBuilderManager<T> addBuilder(Player player, ObjectBuilder<T> builder) {
        addBuilder(player.getUniqueId(), builder);
        return this;
    }

    @NotNull
    public ObjectBuilderManager<T> removeBuilder(UUID uuid) {
        ObjectBuilder<T> builder = builders.get(uuid);
        if (builder == null)
            return this;
        Inventory key = builder.getInventory();
        inventories.remove(key);
        builders.remove(uuid);
        return this;
    }

    @NotNull
    public ObjectBuilderManager<T> removeBuilder(Player player) {
        removeBuilder(player.getUniqueId());
        return this;
    }

    @NotNull
    public ObjectBuilderManager<T> setBuilderBiFunction(BiFunction<UUID, ObjectDirector<T>, ObjectBuilder<T>> function) {
        builderBiFunction = function;
        return this;
    }

    @NotNull
    public String getFileKey() {
        return fileKey;
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