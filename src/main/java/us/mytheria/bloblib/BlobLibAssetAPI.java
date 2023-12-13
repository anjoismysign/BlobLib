package us.mytheria.bloblib;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.action.Action;
import us.mytheria.bloblib.api.*;
import us.mytheria.bloblib.entities.inventory.*;
import us.mytheria.bloblib.entities.message.BlobSound;
import us.mytheria.bloblib.managers.*;

import java.io.File;
import java.util.Optional;

/**
 * Please use the API classes instead of this class.
 */
public class BlobLibAssetAPI {
    private static BlobLibAssetAPI instance;
    private final BlobLibSoundAPI soundAPI;
    private final BlobLibInventoryAPI inventoryAPI;
    private final BlobLibActionAPI actionAPI;
    private final BlobLibTagAPI tagAPI;
    private final BlobLibMessageAPI messageAPI;
    private final BlobLibTranslatableAPI translatableAPI;

    private BlobLibAssetAPI(BlobLib plugin) {
        this.soundAPI = BlobLibSoundAPI.getInstance(plugin);
        this.inventoryAPI = BlobLibInventoryAPI.getInstance(plugin);
        this.actionAPI = BlobLibActionAPI.getInstance(plugin);
        this.tagAPI = BlobLibTagAPI.getInstance(plugin);
        this.messageAPI = BlobLibMessageAPI.getInstance(plugin);
        this.translatableAPI = BlobLibTranslatableAPI.getInstance(plugin);
    }

    public static BlobLibAssetAPI getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibAssetAPI.instance = new BlobLibAssetAPI(plugin);
        }
        return instance;
    }

    public static BlobLibAssetAPI getInstance() {
        return getInstance(null);
    }

    public BlobLibSoundAPI getSoundAPI() {
        return soundAPI;
    }

    public BlobLibInventoryAPI getInventoryAPI() {
        return inventoryAPI;
    }

    public BlobLibActionAPI getActionAPI() {
        return actionAPI;
    }

    public BlobLibTagAPI getTagAPI() {
        return tagAPI;
    }

    public BlobLibMessageAPI getMessageAPI() {
        return messageAPI;
    }

    public BlobLibTranslatableAPI getTranslatableAPI() {
        return translatableAPI;
    }

    @Deprecated
    public static InventoryManager getInventoryManager() {
        return BlobLibInventoryAPI.getInstance().getInventoryManager();
    }

    @Deprecated
    public static MessageManager getMessageManager() {
        return BlobLibMessageAPI.getInstance().getMessageManager();
    }

    @Deprecated
    public static ActionManager getActionManager() {
        return BlobLibActionAPI.getInstance().getActionManager();
    }

    @Deprecated
    public static SoundManager getSoundManager() {
        return BlobLibSoundAPI.getInstance().getSoundManager();
    }

    @Deprecated
    public static InventoryBuilderCarrier<InventoryButton> getInventoryBuilderCarrier(String key) {
        return getInventoryManager().getInventoryBuilderCarrier(key);
    }

    @Deprecated
    public static BlobInventory getBlobInventory(String key) {
        return getInventoryManager().getInventory(key);
    }

    @Deprecated
    public static InventoryBuilderCarrier<MetaInventoryButton> getMetaInventoryBuilderCarrier(String key) {
        return getInventoryManager().getMetaInventoryBuilderCarrier(key);
    }

    @Deprecated
    public static MetaBlobInventory getMetaBlobInventory(String key) {
        return getInventoryManager().getMetaInventory(key);
    }

    @Deprecated
    public static Optional<MetaInventoryShard> hasMetaInventoryShard(String type) {
        return Optional.ofNullable(getInventoryManager().getMetaInventoryShard(type));
    }

    @Deprecated
    @Nullable
    public static Action<Entity> getAction(String key) {
        return getActionManager().getAction(key);
    }

    @Deprecated
    public static void sendMessage(String key, Player player) {
        getMessageManager().send(player, key);
    }

    @Deprecated
    public static BlobSound getSound(String key) {
        return getSoundManager().getSound(key);
    }

    @Deprecated
    public static void playSound(String key, Player player) {
        getSoundManager().play(player, key);
    }


    @Deprecated
    public static File getMessagesDirectory() {
        return BlobLibMessageAPI.getInstance().getMessagesDirectory();
    }

    @Deprecated
    public static String getMessagesFilePath() {
        return BlobLibMessageAPI.getInstance().getMessagesFilePath();
    }

    @Deprecated
    public static File getSoundsDirectory() {
        return BlobLibSoundAPI.getInstance().getSoundsDirectory();
    }

    @Deprecated
    public static String getSoundsFilePath() {
        return BlobLibSoundAPI.getInstance().getSoundsFilePath();
    }

    @Deprecated
    public static File getInventoriesDirectory() {
        return BlobLibInventoryAPI.getInstance().getInventoriesDirectory();
    }

    @Deprecated
    public static String getInventoriesFilePath() {
        return BlobLibInventoryAPI.getInstance().getInventoriesFilePath();
    }

    @Deprecated
    public static BlobInventory buildInventory(String fileName) {
        BlobInventory inventory = BlobLibAssetAPI.getInventoryManager().cloneInventory(fileName);
        if (inventory == null) {
            throw new NullPointerException("Inventory '" + fileName + "' not found");
        }
        return inventory;
    }
}
