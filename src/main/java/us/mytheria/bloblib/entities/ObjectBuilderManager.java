package us.mytheria.bloblib.entities;

import net.md_5.bungee.api.ChatColor;
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

public class ObjectBuilderManager<T> extends Manager {
    protected String title;

    private HashMap<UUID, ObjectBuilder<T>> builders;
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

    public ObjectBuilder<T> getOrDefault(UUID uuid) {
        ObjectBuilder<T> objectBuilder = builders.get(uuid);
        if (objectBuilder == null) {
            objectBuilder = builderFunction.apply(uuid);
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