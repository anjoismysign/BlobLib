package us.mytheria.bloblib.entities;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.api.BlobLibInventoryAPI;
import us.mytheria.bloblib.entities.inventory.BlobInventory;
import us.mytheria.bloblib.entities.inventory.ObjectBuilder;
import us.mytheria.bloblib.managers.*;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;

public class ObjectBuilderManager<T extends BlobObject> extends Manager {
    protected String title;

    private HashMap<UUID, ObjectBuilder<T>> builders;
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
        selPosListenerManager = getManagerDirector().getPositionListenerManager();
        chatManager = getManagerDirector().getChatListenerManager();
        dropListenerManager = getManagerDirector().getDropListenerManager();
        selectorListenerManager = getManagerDirector().getSelectorManager();
        this.objectDirector = Objects.requireNonNull(objectDirector, "Object director cannot be null.");
        this.fileKey = Objects.requireNonNull(fileKey, "File key cannot be null.");
        update();
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
        return this;
    }

    @NotNull
    public ObjectBuilderManager<T> addBuilder(Player player, ObjectBuilder<T> builder) {
        addBuilder(player.getUniqueId(), builder);
        return this;
    }

    @NotNull
    public ObjectBuilderManager<T> removeBuilder(UUID uuid) {
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