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
    private String titleKey;
    private Function<UUID, ObjectBuilder<T>> builderFunction;

    public ObjectBuilderManager(ManagerDirector managerDirector) {
        super(managerDirector);
    }

    public ObjectBuilderManager(ManagerDirector managerDirector,
                                String titleKey,
                                Function<UUID, ObjectBuilder<T>> builderFunction) {
        super(managerDirector);
        this.titleKey = titleKey;
        this.builderFunction = builderFunction;
    }

    @Override
    public void loadInConstructor() {
        selPosListenerManager = getManagerDirector().getPositionListenerManager();
        chatManager = getManagerDirector().getChatListenerManager();
        dropListenerManager = getManagerDirector().getDropListenerManager();
        selectorListenerManager = getManagerDirector().getSelectorManager();
        update();
        this.builders = new HashMap<>();
    }

    @Override
    public void reload() {
        update();
        this.builders = new HashMap<>();
    }

    public void update() {
        Optional<File> file = getManagerDirector().getFileManager().searchFile(titleKey);
        if (file.isEmpty())
            throw new RuntimeException("titleKey not found: " + titleKey);
        YamlConfiguration inventory = YamlConfiguration.loadConfiguration(file.get());
        /*By default, all BlobInventorie's are forced to have Title, else
        they wouldn't load.*/
        this.title = ChatColor.translateAlternateColorCodes('&',
                inventory.getString("Title"));
    }

    public ObjectBuilder<T> getOrDefault(UUID uuid) {
        ObjectBuilder<T> builder = builders.get(uuid);
        if (builder == null) {
            builder = builderFunction.apply(uuid);
            builders.put(uuid, builder);
        }
        return builder;
    }

    public ObjectBuilder<T> getOrDefault(Player player) {
        return getOrDefault(player.getUniqueId());
    }

    public void addBuilder(UUID uuid, ObjectBuilder<T> builder) {
        builders.put(uuid, builder);
    }

    public void removeBuilder(UUID uuid) {
        builders.remove(uuid);
    }

    public void removeBuilder(Player player) {
        removeBuilder(player.getUniqueId());
    }

    public String getTitleKey() {
        return titleKey;
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