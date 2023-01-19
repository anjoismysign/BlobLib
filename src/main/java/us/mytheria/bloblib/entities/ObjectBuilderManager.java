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
    /*
    Pongamos el ejemplo de que usaremos de ObjectBuilder el objeto abstracto
    Reward que luego se extiende en PermissionsReward, CashReward e ItemStackReward.
    Tengo que tener una variable llamada builders que me permita acceder al ObjectBuilder
    que tenga el jugador (siendo el UUID del jugador el key y el ObjectBuilder el value).
    No solo eso, me tiene que permitir colocar cualquiera de los tres tipos de Reward
    dentro de builders.
    El plan sería investigar cuál es la función que agrega un ObjectBuilder a builders
    y asegurarme que la variable builders sea de ObjectBuilder<Reward> ya que cualquiera
    de los tres tipos extienden Reward.

    Sugerencia: crear un nuevo HashMap en el que key sea String y value
    sea un HashMap de key UUID y value ObjectBuilder<>.
     */
    private HashMap<String, HashMap<UUID, ObjectBuilder<T>>> builders;
    private ChatListenerManager chatManager;
    private DropListenerManager dropListenerManager;
    private SelectorListenerManager selectorListenerManager;
    private SelPosListenerManager selPosListenerManager;
    private String fileKey;
    private HashMap<String, Function<UUID, ObjectBuilder<T>>> builderFunctions;

    public ObjectBuilderManager(ManagerDirector managerDirector) {
        super(managerDirector);
    }

    public ObjectBuilderManager(ManagerDirector managerDirector,
                                String fileKey,
                                Function<UUID, ObjectBuilder<T>> builderFunction) {
        super(managerDirector);
        this.fileKey = fileKey;
        this.builderFunctions = new HashMap<>();
        this.builderFunctions.put("default", builderFunction);
    }

    @Override
    public void loadInConstructor() {
        selPosListenerManager = getManagerDirector().getPositionListenerManager();
        chatManager = getManagerDirector().getChatListenerManager();
        dropListenerManager = getManagerDirector().getDropListenerManager();
        selectorListenerManager = getManagerDirector().getSelectorManager();
        update();
        this.builders = new HashMap<>();
        this.builders.put("default", new HashMap<>());
    }

    @Override
    public void reload() {
        update();
        this.builders = new HashMap<>();
        this.builders.put("default", new HashMap<>());
    }

    public void update() {
        Optional<File> file = getManagerDirector().getFileManager().searchFile(fileKey);
        if (file.isEmpty())
            throw new RuntimeException("File not found by key '" + fileKey + "'");
        YamlConfiguration inventory = YamlConfiguration.loadConfiguration(file.get());
        /*By default, all BlobInventorie's are forced to have Title, else
        they wouldn't load.*/
        this.title = ChatColor.translateAlternateColorCodes('&',
                inventory.getString("Title"));
    }

    public ObjectBuilder<T> getOrDefault(UUID uuid, String builderType) {
        ObjectBuilder<T> builder = builders.get(builderType).get(uuid);
        if (builder == null) {
            builder = builderFunctions.get(builderType).apply(uuid);
            builders.get(builderType).put(uuid, builder);
        }
        return builder;
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

    public void addBuilder(UUID uuid, ObjectBuilder<T> builder, String builderType) {
        if (builders.containsKey(builderType)) {
            builders.get(builderType).put(uuid, builder);
        } else {
            builders.put(builderType, new HashMap<>());
            builders.get(builderType).put(uuid, builder);
        }
    }

    public void addBuilder(UUID uuid, ObjectBuilder<T> builder) {
        addBuilder(uuid, builder, "default");
    }

    public void addBuilder(Player player, ObjectBuilder<T> builder, String builderType) {
        addBuilder(player.getUniqueId(), builder, builderType);
    }

    public void addBuilder(Player player, ObjectBuilder<T> builder) {
        addBuilder(player.getUniqueId(), builder);
    }

    public void removeBuilder(UUID uuid, String builderType) {
        if (builders.containsKey(builderType)) {
            builders.get(builderType).remove(uuid);
        }
    }

    public void removeBuilder(UUID uuid) {
        removeBuilder(uuid, "default");
    }

    public void removeBuilder(Player player, String builderType) {
        removeBuilder(player.getUniqueId(), builderType);
    }

    public void removeBuilder(Player player) {
        removeBuilder(player.getUniqueId());
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