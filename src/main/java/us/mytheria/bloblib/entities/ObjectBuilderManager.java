package us.mytheria.bloblib.entities;

import org.bukkit.entity.Player;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.inventory.BlobInventory;
import us.mytheria.bloblib.entities.inventory.ObjectBuilder;
import us.mytheria.bloblib.managers.*;

import java.util.HashMap;
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
        this.objectDirector = objectDirector;
        this.fileKey = fileKey;
        update();
    }

    @Override
    public void loadInConstructor() {
        selPosListenerManager = getManagerDirector().getPositionListenerManager();
        chatManager = getManagerDirector().getChatListenerManager();
        dropListenerManager = getManagerDirector().getDropListenerManager();
        selectorListenerManager = getManagerDirector().getSelectorManager();
    }

    public ObjectDirector<T> getObjectDirector() {
        return objectDirector;
    }

    @Override
    public void reload() {
        update();
    }

    public void update() {
        this.builders = new HashMap<>();
        BlobInventory inventory = BlobLibAssetAPI.getBlobInventory(fileKey);
        this.title = inventory.getTitle();
//        Optional<File> file = getManagerDirector().getFileManager().searchFile(fileKey);
//        if (file.isEmpty())
//            throw new RuntimeException("File not found by key '" + fileKey + "'");
//        YamlConfiguration inventory = YamlConfiguration.loadConfiguration(file.get());
        /*By default, all BlobInventorie's are forced to have Title, else
        they wouldn't load.*/
//        if (!inventory.contains("Title"))
//            throw new RuntimeException("Inventory file '" + fileKey + "' does not have a title.");
//        this.title = ChatColor.translateAlternateColorCodes('&',
//                inventory.getString("Title"));
    }

    public ObjectBuilder<T> getOrDefault(UUID uuid) {
        ObjectBuilder<T> objectBuilder = builders.get(uuid);
        if (objectBuilder == null) {
            objectBuilder = builderBiFunction.apply(uuid, getObjectDirector());
            builders.put(uuid, objectBuilder);
        }
        return objectBuilder;
    }

    public ObjectBuilder<T> getOrDefault(Player player) {
        return getOrDefault(player.getUniqueId());
    }

    public ObjectBuilderManager<T> addBuilder(UUID uuid, ObjectBuilder<T> builder) {
        builders.put(uuid, builder);
        return this;
    }

    public ObjectBuilderManager<T> addBuilder(Player player, ObjectBuilder<T> builder) {
        addBuilder(player.getUniqueId(), builder);
        return this;
    }

    public ObjectBuilderManager<T> removeBuilder(UUID uuid) {
        builders.remove(uuid);
        return this;
    }

    public ObjectBuilderManager<T> removeBuilder(Player player) {
        removeBuilder(player.getUniqueId());
        return this;
    }

    public ObjectBuilderManager<T> setBuilderBiFunction(BiFunction<UUID, ObjectDirector<T>, ObjectBuilder<T>> function) {
        builderBiFunction = function;
        return this;
    }

    public String getFileKey() {
        return fileKey;
    }

    public DropListenerManager getDropListenerManager() {
        return dropListenerManager;
    }

    public ChatListenerManager getChatManager() {
        return chatManager;
    }

    public SelectorListenerManager getSelectorListenerManager() {
        return selectorListenerManager;
    }

    public SelPosListenerManager getSelPosListenerManager() {
        return selPosListenerManager;
    }
}