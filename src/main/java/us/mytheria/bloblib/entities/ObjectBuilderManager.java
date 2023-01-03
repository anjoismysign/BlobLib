package us.mytheria.bloblib.entities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import us.mytheria.bloblib.entities.inventory.ObjectBuilder;
import us.mytheria.bloblib.entities.manager.Manager;
import us.mytheria.bloblib.entities.manager.ManagerDirector;
import us.mytheria.bloblib.managers.ChatListenerManager;
import us.mytheria.bloblib.managers.DropListenerManager;
import us.mytheria.bloblib.managers.SelPosListenerManager;
import us.mytheria.bloblib.managers.SelectorListenerManager;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class ObjectBuilderManager<T> extends Manager implements Listener {
    protected String title;
    private HashMap<UUID, ObjectBuilder<T>> builders;
    private ChatListenerManager chatManager;
    private DropListenerManager dropListenerManager;
    //    private ShopArticleManager shopArticleManager;
    private SelectorListenerManager selectorListenerManager;
    private SelPosListenerManager selPosListenerManager;
    private Supplier<String> titleSupplier;

    public ObjectBuilderManager(ManagerDirector managerDirector) {
        super(managerDirector);
    }

    public ObjectBuilderManager(ManagerDirector managerDirector, Supplier<String> titleSupplier) {
        super(managerDirector);
        this.titleSupplier = titleSupplier;
    }

    @Override
    public void loadInConstructor() {
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
        selPosListenerManager = getManagerDirector().getPositionListenerManager();
        chatManager = getManagerDirector().getChatListenerManager();
        dropListenerManager = getManagerDirector().getDropListenerManager();
//        shopArticleManager = getManagerDirector().getShopArticleManager();
        selectorListenerManager = getManagerDirector().getSelectorManager();
        update();
        this.builders = new HashMap<>();
    }

    @Override
    public void reload() {
        update();
        this.builders = new HashMap<>();
    }

//    @EventHandler
//    public void onClick(InventoryClickEvent e) {
//        String invname = e.getView().getTitle();
//        if (!invname.equals(title)) {
//            return;
//        }
//        int slot = e.getRawSlot();
//        Player player = (Player) e.getWhoClicked();
//        ShopArticleBuilder builder = getOrDefault(player.getUniqueId());
//        if (slot >= builder.getSize()) {
//            return;
//        }
//        e.setCancelled(true);
//        builder.handle(slot, player);
//    }

    public abstract void update();
//    {
//        FileManager fileManager = getManagerDirector().getFileManager();
//        YamlConfiguration inventories = fileManager.getYml(fileManager.inventoriesFile());
//        this.title = ChatColor.translateAlternateColorCodes('&',
//                inventories.getString("ShopArticleBuilder.Title"));
//    }

//    public ShopArticleBuilder getOrDefault(UUID uuid) {
//        ShopArticleBuilder builder = builders.get(uuid);
//        if (builder == null) {
//            builder = ShopArticleBuilder.build(uuid);
//            builders.put(uuid, builder);
//        }
//        return builder;
//    }

//    public ShopArticleBuilder getOrDefault(Player player) {
//        return getOrDefault(player.getUniqueId());
//    }

    public void addBuilder(UUID uuid, ObjectBuilder<T> builder) {
        builders.put(uuid, builder);
    }

    public void removeBuilder(UUID uuid) {
        builders.remove(uuid);
    }

    public void removeBuilder(Player player) {
        removeBuilder(player.getUniqueId());
    }

    public Supplier<String> getTitleSupplier() {
        return titleSupplier;
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