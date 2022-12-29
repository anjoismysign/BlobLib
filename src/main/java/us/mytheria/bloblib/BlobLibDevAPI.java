package us.mytheria.bloblib;

import me.anjoismysign.anjo.entities.Result;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import us.mytheria.bloblib.entities.listeners.BlobChatListener;
import us.mytheria.bloblib.entities.listeners.BlobDropListener;
import us.mytheria.bloblib.entities.listeners.BlobSelPosListener;
import us.mytheria.bloblib.utilities.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class BlobLibDevAPI {
    private static final BlobLib main = BlobLib.getInstance();

    /**
     * @return The messages file
     */
    public static File getMessagesFile() {
        return main.getFileManager().messagesFile();
    }

    /**
     * @return The messages file path
     */
    public static String getMessagesFilePath() {
        return main.getFileManager().messagesFile().getPath();
    }

    /**
     * @return The sounds file
     */
    public static File getSoundsFile() {
        return main.getFileManager().soundsFile();
    }

    /**
     * @return The sounds file path
     */
    public static String getSoundsFilePath() {
        return main.getFileManager().soundsFile().getPath();
    }

    /**
     * @param fileName The name of the file
     * @param plugin   The plugin
     * @return The Result of the file. If the file didn't exist and no exceptions were found, Result will be valid.
     */
    public static Result<File> addDefaultMessagesFile(String fileName, Plugin plugin) {
        File path = new File(getMessagesFile() + "/" + plugin.getName());
        File file = new File(path +
                "/" + fileName + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                ResourceUtil.updateYml(path, "/temp" + fileName + ".yml", fileName + ".yml", file, plugin);
                return Result.valid(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Result.invalid(file);
    }

    /**
     * @param fileName The name of the file
     * @param plugin   The plugin
     * @return The Result of the file. If the file didn't exist and no exceptions were found, Result will be valid.
     */
    public static Result<File> addDefaultSoundsFile(String fileName, Plugin plugin) {
        File path = new File(getSoundsFile() + "/" + plugin.getName());
        File file = new File(path +
                "/" + fileName + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                ResourceUtil.updateYml(path, "/temp" + fileName + ".yml", fileName + ".yml", file, plugin);
                return Result.valid(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Result.invalid(file);
    }

    /**
     * Adds a new chat listener
     *
     * @param player            The player
     * @param timeout           The timeout
     * @param consumer          The consumer. The String is the message sent by the player
     * @param timeoutMessageKey The timeout message key
     * @param timerMessageKey   The timer message key
     */
    public static void addChatListener(Player player, long timeout, Consumer<String> consumer,
                                       String timeoutMessageKey, String timerMessageKey) {
        BlobLib.getInstance().getChatManager().addChatListener(player,
                BlobChatListener.smart(player, timeout, consumer, timeoutMessageKey, timerMessageKey));
    }

    /**
     * Adds a new drop listener
     *
     * @param player          The player
     * @param consumer        The consumer. The ItemStack is the item dropped.
     * @param timerMessageKey The timer message key
     */
    public static void addDropListener(Player player, Consumer<ItemStack> consumer,
                                       String timerMessageKey) {
        BlobLib.getInstance().getDropListenerManager().addDropListener(player,
                BlobDropListener.smart(player, consumer, timerMessageKey));
    }

    /**
     * Adds a new position listener
     *
     * @param player            The player
     * @param timeout           The timeout
     * @param consumer          The consumer. The Block is the block clicked.
     * @param timeoutMessageKey The timeout message key
     * @param timerMessageKey   The timer message key
     */
    public static void addPositionListener(Player player, long timeout, Consumer<Block> consumer,
                                           String timeoutMessageKey, String timerMessageKey) {
        BlobLib.getInstance().getPositionManager().addPositionListener(player,
                BlobSelPosListener.smart(player, timeout, consumer, timeoutMessageKey, timerMessageKey));
    }
}
