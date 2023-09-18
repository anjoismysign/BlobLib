package us.mytheria.bloblib.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.BlobEditor;
import us.mytheria.bloblib.entities.inventory.VariableSelector;
import us.mytheria.bloblib.entities.listeners.*;

import java.util.function.Consumer;

public class BlobLibListenerAPI {
    private static BlobLibListenerAPI instance;
    private final BlobLib plugin;

    private BlobLibListenerAPI(BlobLib plugin) {
        this.plugin = plugin;
    }

    public static BlobLibListenerAPI getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibListenerAPI.instance = new BlobLibListenerAPI(plugin);
        }
        return instance;
    }

    public static BlobLibListenerAPI getInstance() {
        return getInstance(null);
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
    public void addChatListener(Player player, long timeout, Consumer<String> consumer,
                                String timeoutMessageKey, String timerMessageKey) {
        plugin.getChatManager().addChatListener(player,
                BlobChatListener.smart(player, timeout, consumer, timeoutMessageKey, timerMessageKey));
    }

    /**
     * Adds a new drop listener
     *
     * @param player          The player
     * @param consumer        The consumer. The ItemStack is the item dropped.
     * @param timerMessageKey The timer message key
     */
    public void addDropListener(Player player, Consumer<ItemStack> consumer,
                                String timerMessageKey) {
        plugin.getDropListenerManager().addDropListener(player,
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
    public void addPositionListener(Player player, long timeout, Consumer<Block> consumer,
                                    String timeoutMessageKey, String timerMessageKey) {
        plugin.getPositionManager().addPositionListener(player,
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
    public <T> void addSelectorListener(Player player, Consumer<T> consumer,
                                        @Nullable String timerMessageKey,
                                        VariableSelector<T> selector) {
        plugin.getSelectorManager().addSelectorListener(player,
                BlobSelectorListener.wise(player, consumer, timerMessageKey, selector));
    }

    public <T> void addEditorListener(Player player, Consumer<T> consumer,
                                      String timerMessageKey,
                                      BlobEditor<T> editor) {
        plugin.getSelectorManager().addEditorListener(player,
                BlobEditorListener.wise(player, consumer, timerMessageKey, editor));
    }
}
