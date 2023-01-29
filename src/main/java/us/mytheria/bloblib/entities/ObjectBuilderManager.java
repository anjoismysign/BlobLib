package us.mytheria.bloblib.entities;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import us.mytheria.bloblib.entities.inventory.ObjectBuilder;
import us.mytheria.bloblib.entities.manager.Manager;
import us.mytheria.bloblib.entities.manager.ManagerDirector;
import us.mytheria.bloblib.managers.ChatListenerManager;
import us.mytheria.bloblib.managers.DropListenerManager;
import us.mytheria.bloblib.managers.SelPosListenerManager;
import us.mytheria.bloblib.managers.SelectorListenerManager;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;

public class ObjectBuilderManager<T> extends Manager {
    protected String title;

    private HashMap<String, HashMap<UUID, ObjectBuilder<T>>> builders;
    private ChatListenerManager chatManager;
    private DropListenerManager dropListenerManager;
    private SelectorListenerManager selectorListenerManager;
    private SelPosListenerManager selPosListenerManager;
    private String fileKey;
    private Function<UUID, ObjectBuilder<T>> builderFunction;

    public ObjectBuilderManager(ManagerDirector managerDirector) {
        super(managerDirector);
    }

    public ObjectBuilderManager(ManagerDirector managerDirector,
                                String fileKey) {
        super(managerDirector);
        this.builders = new HashMap<>();
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

    @Override
    public void reload() {
        update();
        this.builders = new HashMap<>();
    }

    public void update() {
        Optional<File> file = getManagerDirector().getFileManager().searchFile(fileKey);
        if (file.isEmpty())
            throw new RuntimeException("File not found by key '" + fileKey + "'");
        YamlConfiguration inventory = YamlConfiguration.loadConfiguration(file.get());
        /*By default, all BlobInventorie's are forced to have Title, else
        they wouldn't load.*/
        if (!inventory.contains("Title"))
            throw new RuntimeException("Inventory file '" + fileKey + "' does not have a title.");
        this.title = ChatColor.translateAlternateColorCodes('&',
                inventory.getString("Title"));
    }

    public ObjectBuilder<T> getOrDefault(UUID uuid, String builderType) {
        //TODO: Remove logger
        Logger logger = Bukkit.getLogger();
        logger.info("getOrDefault");
        boolean buildersNull = builders == null;
        if (buildersNull) {
            logger.info("builders == null");
            throw new RuntimeException("builders null");
        }
        HashMap<UUID, ObjectBuilder<T>> builderMap = builders.get(builderType);
        if (builderMap == null) {
            builderMap = new HashMap<>();
            builders.put(builderType, builderMap);
            logger.severe("Builder type '" + builderType + "' was not initialized.");
            return getOrDefault(uuid, builderType);
        }
        ObjectBuilder<T> objectBuilder = builders.get(builderType).get(uuid);
        if (objectBuilder == null) {
            logger.info("1");
            objectBuilder = builderFunction.apply(uuid);
            builders.get(builderType).put(uuid, objectBuilder);
        }
        return objectBuilder;
    }

    public ObjectBuilder<T> getOrDefault(UUID uuid) {
        return getOrDefault(uuid, "default");
    }

    public ObjectBuilder<T> getOrDefault(Player player, String builderType) {
        return getOrDefault(player.getUniqueId(), builderType);
    }

    public ObjectBuilder<T> getOrDefault(Player player) {
        return getOrDefault(player.getUniqueId());
    }

    public ObjectBuilderManager<T> addBuilder(UUID uuid, ObjectBuilder<T> builder, String builderType) {
        if (builders.containsKey(builderType)) {
            builders.get(builderType).put(uuid, builder);
        } else {
            builders.put(builderType, new HashMap<>());
            builders.get(builderType).put(uuid, builder);
        }
        return this;
    }

    public ObjectBuilderManager<T> addBuilder(UUID uuid, ObjectBuilder<T> builder) {
        addBuilder(uuid, builder, "default");
        return this;
    }

    public ObjectBuilderManager<T> addBuilder(Player player, ObjectBuilder<T> builder, String builderType) {
        addBuilder(player.getUniqueId(), builder, builderType);
        return this;
    }

    public ObjectBuilderManager<T> addBuilder(Player player, ObjectBuilder<T> builder) {
        addBuilder(player.getUniqueId(), builder);
        return this;
    }

    public ObjectBuilderManager<T> removeBuilder(UUID uuid, String builderType) {
        if (builders.containsKey(builderType)) {
            builders.get(builderType).remove(uuid);
        }
        return this;
    }

    public ObjectBuilderManager<T> removeBuilder(UUID uuid) {
        removeBuilder(uuid, "default");
        return this;
    }

    public ObjectBuilderManager<T> removeBuilder(Player player, String builderType) {
        removeBuilder(player.getUniqueId(), builderType);
        return this;
    }

    public ObjectBuilderManager<T> removeBuilder(Player player) {
        removeBuilder(player.getUniqueId());
        return this;
    }

    public ObjectBuilderManager<T> setBuilderFunction(Function<UUID, ObjectBuilder<T>> function) {
        builderFunction = function;
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