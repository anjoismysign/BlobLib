package us.mytheria.bloblib;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.entities.inventory.VariableSelector;
import us.mytheria.bloblib.entities.listeners.BlobChatListener;
import us.mytheria.bloblib.entities.listeners.BlobDropListener;
import us.mytheria.bloblib.entities.listeners.BlobSelPosListener;
import us.mytheria.bloblib.entities.listeners.BlobSelectorListener;

import java.io.File;
import java.util.function.Consumer;

/**
 * @author anjoismysign
 * It's meant to hold quick/static methods that later are meant to be
 * moved to a different class, such as BlobLibAPI.
 * Consider all methods as deprecated and subject to change.
 */
public class BlobLibDevAPI {
    private static final BlobLib main = BlobLib.getInstance();

    /**
     * @return The messages file
     * @deprecated Use {@link #getMessagesDirectory()} instead
     * to avoid confusion.
     */
    @Deprecated
    @NotNull
    public static File getMessagesFile() {
        return main.getFileManager().messagesDirectory();
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
     * @deprecated Use {@link #getSoundsDirectory()} instead
     * to avoid confusion.
     */
    @Deprecated
    @NotNull
    public static File getSoundsFile() {
        return main.getFileManager().soundsDirectory();
    }

    /**
     * Retrieves a file from the inventories' directory.
     *
     * @return The inventories file
     * @deprecated Use {@link #getInventoriesDirectory()} instead
     * to avoid confusion.
     */
    @Deprecated
    @NotNull
    public static File getInventoriesFile() {
        return main.getFileManager().inventoriesDirectory();
    }

    /**
     * @return The sounds file
     */
    @NotNull
    public static File getSoundsDirectory() {
        return main.getFileManager().soundsDirectory();
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
     * @return The sounds file path
     */
    @NotNull
    public static String getSoundsFilePath() {
        return main.getFileManager().soundsDirectory().getPath();
    }

    // DEPRECATED ALL BELOW
//    private static Result<File> conventionAddDefaultInventoryFile(String fileName, Plugin plugin) {
//        fileName = NamingConventions.toSnakeCase(fileName);
//        File path = new File(getInventoriesDirectory() + "/" + plugin.getName());
//        File file = new File(path +
//                "/" + fileName + ".yml");
//        if (!file.exists()) {
//            try {
//                file.createNewFile();
//                ResourceUtil.updateYml(path, "/temp" + fileName + ".yml", fileName + ".yml", file, plugin);
//                return Result.valid(file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return Result.invalid(file);
//    }
//
//    /**
//     * @param fileName The name of the file
//     * @param plugin   The plugin
//     * @return The Result of the file. If the file didn't exist and no exceptions were found, Result will be valid.
//     * @deprecated Use {@link #conventionAddDefaultMessagesFile(String, Plugin)} to
//     * ensure that a naming convention is followed.
//     */
//    @Deprecated
//    public static Result<File> addDefaultMessagesFile(String fileName, Plugin plugin) {
//        File path = new File(getMessagesDirectory() + "/" + plugin.getName());
//        File file = new File(path +
//                "/" + fileName + ".yml");
//        if (!file.exists()) {
//            try {
//                file.createNewFile();
//                ResourceUtil.updateYml(path, "/temp" + fileName + ".yml", fileName + ".yml", file, plugin);
//                return Result.valid(file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return Result.invalid(file);
//    }
//
//    /**
//     * @param fileName The name of the file. Is converted to snake_case
//     * @param plugin   The plugin
//     * @return The Result of the file. If the file didn't exist and no exceptions were found, Result will be valid.
//     */
//    public static Result<File> conventionAddDefaultMessagesFile(String fileName, Plugin plugin) {
//        String snakeCase = NamingConventions.toSnakeCase(fileName);
//        return addDefaultMessagesFile(snakeCase, plugin);
//    }
//
//    /**
//     * @param fileName The name of the file
//     * @param plugin   The plugin
//     * @return The Result of the file. If the file didn't exist and no exceptions were found, Result will be valid.
//     */
//    @Deprecated
//    public static Result<File> addDefaultSoundsFile(String fileName, Plugin plugin) {
//        File path = new File(getSoundsDirectory() + "/" + plugin.getName());
//        File file = new File(path +
//                "/" + fileName + ".yml");
//        if (!file.exists()) {
//            try {
//                file.createNewFile();
//                ResourceUtil.updateYml(path, "/temp" + fileName + ".yml", fileName + ".yml", file, plugin);
//                return Result.valid(file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return Result.invalid(file);
//    }
//
//    /**
//     * @param fileName The name of the file. Is converted to snake_case
//     * @param plugin   The plugin
//     * @return The Result of the file. If the file didn't exist and no exceptions were found, Result will be valid.
//     */
//    public static Result<File> conventionAddDefaultSoundsFile(String fileName, Plugin plugin) {
//        String snakeCase = NamingConventions.toSnakeCase(fileName);
//        return addDefaultSoundsFile(snakeCase, plugin);
//    }

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

    /**
     * Adds a new selector listener
     *
     * @param player          The player
     * @param consumer        The consumer. The argument is the item selected.
     * @param timerMessageKey The timer message key
     * @param selector        The selector
     * @param <T>             The type of the selector
     */
    public static <T> void addSelectorListener(Player player, Consumer<T> consumer,
                                               String timerMessageKey,
                                               VariableSelector<T> selector) {
        BlobLib.getInstance().getSelectorManager().addSelectorListener(player,
                BlobSelectorListener.smart(player, consumer, timerMessageKey, selector));
    }
}
