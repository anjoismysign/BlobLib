package us.mytheria.bloblib.entities.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.inventory.VariableSelector;
import us.mytheria.bloblib.entities.message.BlobMessage;
import us.mytheria.bloblib.managers.SelectorListenerManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BlobSelectorListener<T> extends SelectorListener<T> {
    private final List<BlobMessage> messages;
    private BukkitTask messageTask;

    /**
     * Will run a SelectorListener which will send messages to player every 10 ticks asynchronously
     *
     * @param owner         The owner of the SelectorListener
     * @param inputRunnable The runnable to run when the SelectorListener receives input
     * @param messages      The messages to send to the player
     * @deprecated Use {@link #wise(Player, Consumer, String, VariableSelector)} instead.
     */
    @Deprecated
    public static <T> BlobSelectorListener<T> build(Player owner, Runnable inputRunnable
            , List<BlobMessage> messages, VariableSelector<T> selector) {
        return new BlobSelectorListener<>(owner.getName(),
                inputRunnable, messages, selector);
    }

    /**
     * Will run a SelectorListener which will send messages to player every 10 ticks asynchronously
     *
     * @param player          The player to send messages to
     * @param consumer        The consumer to run when the SelectorListener receives input
     * @param timerMessageKey The key of the message to send to the player
     * @param selector        The selector to use
     * @param <T>             The type of the input
     * @return The SelectorListener
     */
    @Deprecated
    public static <T> BlobSelectorListener<T> smart(Player player, Consumer<T> consumer,
                                                    String timerMessageKey,
                                                    VariableSelector<T> selector) {
        BlobLib main = BlobLib.getInstance();
        SelectorListenerManager selectorManager = main.getSelectorManager();
        Optional<BlobMessage> timerMessage = Optional.ofNullable(BlobLibAssetAPI.getMessage(timerMessageKey));
        List<BlobMessage> messages = timerMessage.map(Collections::singletonList).orElse(Collections.emptyList());
        return new BlobSelectorListener<>(player.getName(), () -> {
            @SuppressWarnings("unchecked") T input = (T) selectorManager.getInput(player);
            selectorManager.removeSelectorListener(player);
            Bukkit.getScheduler().runTask(main, () -> {
                if (player == null || !player.isOnline()) {
                    return;
                }
                consumer.accept(input);
            });
        }, messages, selector);
    }

    /**
     * Will run a SelectorListener which will send messages to player every 10 ticks asynchronously.
     * Will check if input is null. If so, will close player's inventory preventing
     * dupe exploits and will also return, not running the consumer.
     * Note that if not null, player's inventory won't be closed so you need to make sure
     * to close it if you need to, preferably in the consumer.
     *
     * @param player          The player to send messages to
     * @param consumer        The consumer to run when the SelectorListener receives input
     * @param timerMessageKey The key of the message to send to the player
     * @param selector        The selector to use
     * @param <T>             The type of the input
     * @return The SelectorListener
     */
    public static <T> BlobSelectorListener<T> wise(Player player, Consumer<T> consumer,
                                                   String timerMessageKey,
                                                   VariableSelector<T> selector) {
        BlobLib main = BlobLib.getInstance();
        SelectorListenerManager selectorManager = main.getSelectorManager();
        Optional<BlobMessage> timerMessage = Optional.ofNullable(BlobLibAssetAPI.getMessage(timerMessageKey));
        List<BlobMessage> messages = timerMessage.map(Collections::singletonList).orElse(Collections.emptyList());
        return new BlobSelectorListener<>(player.getName(), selectorListener -> {
            T input = selectorListener.getInput();
            selectorManager.removeSelectorListener(player);
            if (input == null) {
                player.closeInventory();
                return;
            }
            Bukkit.getScheduler().runTask(main, () -> {
                if (player == null || !player.isOnline()) {
                    return;
                }
                consumer.accept(input);
            });
        }, messages, selector);
    }

    /**
     * Will run a SelectorListener which will send messages to player every 10 ticks asynchronously
     *
     * @param owner         The player's name which is owner of the SelectorListener
     * @param inputRunnable The runnable to run when the SelectorListener receives input
     * @param messages      The messages to send to the player
     */
    @Deprecated
    private BlobSelectorListener(String owner, Runnable inputRunnable, List<BlobMessage> messages,
                                 VariableSelector<T> selector) {
        super(owner, inputRunnable, selector);
        this.messages = messages;
    }

    private BlobSelectorListener(String owner, Consumer<SelectorListener<T>> inputConsumer,
                                 List<BlobMessage> messages, VariableSelector<T> selector) {
        super(owner, inputConsumer, selector);
        this.messages = messages;
    }

    @Override
    public void runTasks() {
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(getOwner());
                if (player == null || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                messages.forEach(message -> message.sendAndPlay(player));
            }
        };
        this.messageTask = bukkitRunnable.runTaskTimerAsynchronously(BlobLib.getInstance(), 0, 10);
    }

    @Override
    public void cancel() {
        messageTask.cancel();
    }

    public List<BlobMessage> getMessages() {
        return messages;
    }
}
