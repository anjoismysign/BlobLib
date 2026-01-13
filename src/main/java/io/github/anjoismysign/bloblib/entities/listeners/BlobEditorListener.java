package io.github.anjoismysign.bloblib.entities.listeners;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.api.BlobLibMessageAPI;
import io.github.anjoismysign.bloblib.entities.BlobEditor;
import io.github.anjoismysign.bloblib.entities.message.BlobMessage;
import io.github.anjoismysign.bloblib.managers.SelectorListenerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BlobEditorListener<T> extends EditorListener<T> {
    private final List<BlobMessage> messages;
    private BukkitTask messageTask;

    /**
     * Will run a SelectorListener which will send messages to player every 10 ticks asynchronously.
     * Will check if input is null. If so, will close player's inventory preventing
     * dupe exploits and will also return, not running the consumer.
     * Note that if not null, player's inventory won't be closed, so you need to make sure
     * to close it if you need to, preferably in the consumer.
     *
     * @param player          The player to send messages to
     * @param consumer        The consumer to run when the SelectorListener receives input
     * @param timerMessageKey The key of the message to send to the player
     * @param selector        The selector to use
     * @param <T>             The type of the input
     * @return The SelectorListener
     */
    public static <T> BlobEditorListener<T> wise(Player player, Consumer<T> consumer,
                                                 String timerMessageKey,
                                                 BlobEditor<T> selector) {
        BlobLib main = BlobLib.getInstance();
        SelectorListenerManager selectorManager = main.getSelectorManager();
        Optional<BlobMessage> timerMessage = Optional.empty();
        if (timerMessageKey != null)
            timerMessage = Optional.ofNullable(BlobLibMessageAPI.getInstance().getMessage(timerMessageKey, player));
        List<BlobMessage> messages = timerMessage.map(Collections::singletonList).orElse(new ArrayList<>());
        return new BlobEditorListener<>(player.getName(), listener -> {
            T input = listener.getInput();
            selectorManager.removeEditorListener(player);
            if (input == null) {
                player.closeInventory();
                return;
            }
            Bukkit.getScheduler().runTask(main, () -> {
                if (!player.isConnected())
                    return;
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
    private BlobEditorListener(String owner, Runnable inputRunnable, List<BlobMessage> messages,
                               BlobEditor<T> selector) {
        super(owner, inputRunnable, selector);
        this.messages = messages;
    }

    private BlobEditorListener(String owner, Consumer<EditorListener<T>> inputConsumer,
                               List<BlobMessage> messages, BlobEditor<T> selector) {
        super(owner, inputConsumer, selector);
        this.messages = messages;
    }

    @Override
    public void runTasks() {
        Player player = Bukkit.getPlayer(getOwner());
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isConnected()) {
                    this.cancel();
                    return;
                }
                if (messages.isEmpty())
                    return;
                for (BlobMessage message : messages) {
                    if (message != null)
                        message.handle(player);
                }
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
