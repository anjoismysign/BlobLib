package us.mytheria.bloblib;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.entities.inventory.BlobInventory;
import us.mytheria.bloblib.entities.inventory.MetaBlobInventory;
import us.mytheria.bloblib.entities.message.BlobSound;
import us.mytheria.bloblib.entities.message.ReferenceBlobMessage;
import us.mytheria.bloblib.managers.InventoryManager;
import us.mytheria.bloblib.managers.MessageManager;
import us.mytheria.bloblib.managers.MetaInventoryShard;
import us.mytheria.bloblib.managers.SoundManager;

import java.io.File;
import java.util.Optional;

public class BlobLibAssetAPI {
    private static final BlobLib main = BlobLib.getInstance();

    /**
     * @return The inventory manager
     */
    public static InventoryManager getInventoryManager() {
        return main.getInventoryManager();
    }

    /**
     * @return The message manager
     */
    public static MessageManager getMessageManager() {
        return main.getMessageManager();
    }

    /**
     * @return The sound manager
     */
    public static SoundManager getSoundManager() {
        return main.getSoundManager();
    }

    /**
     * @param key Key that points to the inventory
     * @return The inventory
     */
    public static BlobInventory getBlobInventory(String key) {
        return getInventoryManager().getInventory(key);
    }

    /**
     * @param key Key that points to the inventory
     * @return The inventory
     */
    public static MetaBlobInventory getMetaBlobInventory(String key) {
        return getInventoryManager().getMetaInventory(key);
    }

    /**
     * Attempts to get a MetaInventoryShard from the given type.
     * If not found, will return an empty optional.
     *
     * @param type The type of the shard
     * @return The shard if found, otherwise an empty optional
     */
    @NotNull
    public static Optional<MetaInventoryShard> hasMetaInventoryShard(String type) {
        return Optional.ofNullable(getInventoryManager().getMetaInventoryShard(type));
    }

    /**
     * @param key The key of the message
     * @return The message
     */
    public static ReferenceBlobMessage getMessage(String key) {
        return getMessageManager().getMessage(key);
    }

    /**
     * @param key    The key of the message
     * @param player The player to send the message to
     */
    public static void sendMessage(String key, Player player) {
        getMessageManager().send(player, key);
    }

    /**
     * @param key The key of the sound
     * @return The sound
     */
    public static BlobSound getSound(String key) {
        return getSoundManager().getSound(key);
    }

    /**
     * @param key    The key of the sound
     * @param player The player to play the sound
     */
    public static void playSound(String key, Player player) {
        getSoundManager().play(player, key);
    }


    /**
     * @return The messages file
     */
    @NotNull
    public static File getMessagesDirectory() {
        return main.getFileManager().messagesDirectory();
    }

    /**
     * @return The messages file path
     */
    @NotNull
    public static String getMessagesFilePath() {
        return main.getFileManager().messagesDirectory().getPath();
    }

    /**
     * @return The sounds file
     */
    @NotNull
    public static File getSoundsDirectory() {
        return main.getFileManager().soundsDirectory();
    }

    /**
     * @return The sounds file path
     */
    @NotNull
    public static String getSoundsFilePath() {
        return main.getFileManager().soundsDirectory().getPath();
    }

    /**
     * Retrieves a file from the inventories' directory.
     *
     * @return The inventories file
     */
    @NotNull
    public static File getInventoriesDirectory() {
        return main.getFileManager().inventoriesDirectory();
    }

    /**
     * @return The inventories file path
     */
    @NotNull
    public static String getInventoriesFilePath() {
        return main.getFileManager().inventoriesDirectory().getPath();
    }

    /**
     * Attempts to build an inventory from the given file name.
     * If the inventory is not found, a NullPointerException is thrown.
     *
     * @param fileName The file name
     * @return The inventory
     */
    public static BlobInventory buildInventory(String fileName) {
        BlobInventory inventory = BlobLibAssetAPI.getInventoryManager().cloneInventory(fileName);
        if (inventory == null) {
            throw new NullPointerException("Inventory '" + fileName + "' not found");
        }
        return inventory;
    }
}
