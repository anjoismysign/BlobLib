package us.mytheria.bloblib.entities.manager;

import org.bukkit.plugin.Plugin;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.BlobFileManager;
import us.mytheria.bloblib.managers.ChatListenerManager;
import us.mytheria.bloblib.managers.DropListenerManager;
import us.mytheria.bloblib.managers.SelPosListenerManager;
import us.mytheria.bloblib.managers.SelectorListenerManager;

import java.util.HashMap;

public abstract class ManagerDirector {
    private final Plugin plugin;
    private final HashMap<String, Manager> managers;
    private final ChatListenerManager chatListenerManager;
    private final SelectorListenerManager selectorListenerManager;
    private final SelPosListenerManager positionListenerManager;
    private final DropListenerManager dropListenerManager;
    private final BlobFileManager blobFileManager;

    public ManagerDirector(Plugin plugin, String fileManagerPathname) {
        this.plugin = plugin;
        this.blobFileManager = new BlobFileManager(this, fileManagerPathname);
        chatListenerManager = BlobLib.getInstance().getChatManager();
        selectorListenerManager = BlobLib.getInstance().getSelectorManager();
        positionListenerManager = BlobLib.getInstance().getPositionManager();
        dropListenerManager = BlobLib.getInstance().getDropListenerManager();
        managers = new HashMap<>();
    }

    public ManagerDirector(Plugin plugin, BlobFileManager fileManager) {
        this.plugin = plugin;
        this.blobFileManager = fileManager;
        chatListenerManager = BlobLib.getInstance().getChatManager();
        selectorListenerManager = BlobLib.getInstance().getSelectorManager();
        positionListenerManager = BlobLib.getInstance().getPositionManager();
        dropListenerManager = BlobLib.getInstance().getDropListenerManager();
        managers = new HashMap<>();
    }

    public void addManager(String key, Manager manager) {
        managers.put(key, manager);
    }

    public void reload() {
    }

    public Manager getManager(String key) {
        return managers.get(key);
    }

    public void unload() {
    }

    public void postWorld() {
    }

    public ChatListenerManager getChatListenerManager() {
        return chatListenerManager;
    }

    public SelectorListenerManager getSelectorManager() {
        return selectorListenerManager;
    }

    public SelPosListenerManager getPositionListenerManager() {
        return positionListenerManager;
    }

    public DropListenerManager getDropListenerManager() {
        return dropListenerManager;
    }

    public BlobFileManager getFileManager() {
        return blobFileManager;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
