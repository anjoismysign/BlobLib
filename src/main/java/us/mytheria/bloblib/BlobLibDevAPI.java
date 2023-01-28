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
     * @deprecated Use {@link BlobLibAssetAPI#getMessagesDirectory()} instead
     * to avoid confusion.
     */
    @Deprecated
    @NotNull
    public static File getMessagesFile() {
        return main.getFileManager().messagesDirectory();
    }

    /**
     * @return The sounds file
     * @deprecated Use {@link BlobLibAssetAPI#getSoundsDirectory()} instead
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
     * @deprecated Use {@link BlobLibAssetAPI#getInventoriesDirectory()} instead
     * to avoid confusion.
     */
    @Deprecated
    @NotNull
    public static File getInventoriesFile() {
        return main.getFileManager().inventoriesDirectory();
    }

    /**
     * Adds a new chat listener
     *
     * @param player            The player
     * @param timeout           The timeout
     * @param consumer          The consumer. The String is the message sent by the player
     * @param timeoutMessageKey The timeout message key
     * @param timerMessageKey   The timer message key
     * @deprecated Use {@link BlobLibAPI#addChatListener(Player, long, Consumer, String, String)}
     * since this class (BlobLibDevAPI) is in subject to change.
     */
    @Deprecated
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
     * @deprecated Use {@link BlobLibAPI#addDropListener(Player, Consumer, String)} instead
     * since this class (BlobLibDevAPI) is in subject to change.
     */
    @Deprecated
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
     * @deprecated Use {@link BlobLibAPI#addPositionListener(Player, long, Consumer, String, String)}
     * since this class (BlobLibDevAPI) is in subject to change.
     */
    @Deprecated
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
     * @deprecated Use {@link BlobLibAPI#addSelectorListener(Player, Consumer, String, VariableSelector)}
     * since this class (BlobLibDevAPI) is in subject to change.
     */
    @Deprecated
    public static <T> void addSelectorListener(Player player, Consumer<T> consumer,
                                               String timerMessageKey,
                                               VariableSelector<T> selector) {
        BlobLib.getInstance().getSelectorManager().addSelectorListener(player,
                BlobSelectorListener.smart(player, consumer, timerMessageKey, selector));
    }
}
