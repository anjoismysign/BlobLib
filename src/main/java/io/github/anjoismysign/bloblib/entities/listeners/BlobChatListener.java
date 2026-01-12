package io.github.anjoismysign.bloblib.entities.listeners;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.api.BlobLibMessageAPI;
import io.github.anjoismysign.bloblib.entities.message.BlobMessage;
import io.github.anjoismysign.bloblib.managers.ChatListenerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author anjoismysign
 * A listener that runs tasks when input is received through chat
 */
public class BlobChatListener extends ChatListener {
    private final List<BlobMessage> messages;
    private BukkitTask messageTask;

    /**
     * Will run a ChatListener which will send messages to player every 10 ticks asynchronously
     *
     * @param owner           The owner of the ChatListener
     * @param timeout         The timeout of the ChatListener
     * @param inputRunnable   The runnable to run when the ChatListener receives input
     * @param timeoutRunnable The runnable to run when the ChatListener times out
     * @param messages        The messages to send to the player
     * @return The ChatListener
     * @deprecated Use {@link #smart(Player, long, Consumer, String, String)} instead
     */
    @Deprecated
    public static BlobChatListener build(Player owner, long timeout, Runnable inputRunnable,
                                         Runnable timeoutRunnable, List<BlobMessage> messages) {
        return new BlobChatListener(owner.getName(), timeout, inputListener -> {
            inputRunnable.run();
        }, timeoutListener -> {
            timeoutRunnable.run();
        }, messages);
    }

    /**
     * Will run a smart ChatListener which will send messages to player every 10 ticks asynchronously
     *
     * @param owner             The owner of the ChatListener
     * @param timeout           The timeout of the ChatListener
     * @param consumer          The consumer to run when the ChatListener receives input
     * @param timeoutMessageKey The message to send when the ChatListener times out
     * @param timerMessageKey   The message to send to the player every 10 ticks
     * @return The ChatListener
     */
    public static BlobChatListener smart(Player owner, long timeout, Consumer<String> consumer,
                                         String timeoutMessageKey, String timerMessageKey) {
        BlobLib main = BlobLib.getInstance();
        ChatListenerManager chatManager = main.getChatManager();
        Optional<BlobMessage> timeoutMessage = Optional.ofNullable(BlobLibMessageAPI.getInstance().getMessage(timeoutMessageKey, owner));
        Optional<BlobMessage> timerMessage = Optional.ofNullable(BlobLibMessageAPI.getInstance().getMessage(timerMessageKey, owner));
        List<BlobMessage> messages = timerMessage.map(Collections::singletonList).orElse(Collections.emptyList());
        List<BlobMessage> timerMessages = new ArrayList<>();
        messages.forEach(message -> timerMessages.add(
                message.modify(s -> s.replace("%world%", owner.getWorld().getName()))
        ));
        UUID uuid = owner.getUniqueId();
        return new BlobChatListener(owner.getName(), timeout,
                inputListener -> {
                    String input = inputListener.getInput();
                    chatManager.removeChatListener(owner);
                    Bukkit.getScheduler().runTask(main, () -> {
                        if (!owner.isConnected()) {
                            return;
                        }
                        consumer.accept(input);
                    });
                },
                timeoutListener -> {
                    chatManager.removeChatListener(owner);
                    timeoutMessage.ifPresent(blobMessage -> blobMessage.handle(owner));
                }, timerMessages);
    }

    /**
     * Will run a ChatListener which will send messages to player every 10 ticks asynchronously
     *
     * @param owner           The player's name which is owner of the ChatListener
     * @param timeout         The timeout of the ChatListener
     * @param inputConsumer   The consumer to run when the ChatListener receives input
     * @param timeoutConsumer The consumer to run when the ChatListener times out
     * @param messages        The messages to send to the player
     */
    private BlobChatListener(String owner, long timeout, Consumer<BlobChatListener> inputConsumer,
                             Consumer<BlobChatListener> timeoutConsumer, List<BlobMessage> messages) {
        super(owner, timeout, inputListener -> inputConsumer
                        .accept((BlobChatListener) inputListener),
                timeoutListener -> timeoutConsumer
                        .accept((BlobChatListener) timeoutListener));
        this.messages = messages;
    }

    /**
     * Runs the ChatListener
     */
    @Override
    public void runTasks() {
        super.runTasks();
        Player player = Bukkit.getPlayer(getOwner());
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isConnected()) {
                    this.cancel();
                    return;
                }
                messages.forEach(message -> message.handle(player));
            }
        };
        this.messageTask = bukkitRunnable.runTaskTimerAsynchronously(BlobLib.getInstance(), 0, 100);
    }

    /**
     * Cancels the ChatListener
     */
    @Override
    public void cancel() {
        getTask().cancel();
        messageTask.cancel();
    }

    /**
     * @return The messages to send to the player
     */
    public List<BlobMessage> getMessages() {
        return messages;
    }
}
