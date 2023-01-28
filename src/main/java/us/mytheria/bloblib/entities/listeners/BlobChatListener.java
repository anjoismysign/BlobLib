package us.mytheria.bloblib.entities.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.message.BlobMessage;
import us.mytheria.bloblib.managers.ChatListenerManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
     */
    public static BlobChatListener build(Player owner, long timeout, Runnable inputRunnable,
                                         Runnable timeoutRunnable, List<BlobMessage> messages) {
        return new BlobChatListener(owner.getName(), timeout, inputRunnable, timeoutRunnable, messages);
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
        Optional<BlobMessage> timeoutMessage = Optional.ofNullable(BlobLibAssetAPI.getMessage(timeoutMessageKey));
        Optional<BlobMessage> timerMessage = Optional.ofNullable(BlobLibAssetAPI.getMessage(timerMessageKey));
        List<BlobMessage> messages = timerMessage.map(Collections::singletonList).orElse(Collections.emptyList());
        return new BlobChatListener(owner.getName(), timeout,
                () -> {
                    String input = chatManager.getInput(owner);
                    chatManager.removeChatListener(owner);
                    Bukkit.getScheduler().runTask(main, () -> {
                        if (owner == null || !owner.isOnline()) {
                            return;
                        }
                        consumer.accept(input);
                    });
                },
                () -> {
                    chatManager.removeChatListener(owner);
                    timeoutMessage.ifPresent(blobMessage -> blobMessage.sendAndPlay(owner));
                }, messages);
    }

    /**
     * Will run a ChatListener which will send messages to player every 10 ticks asynchronously
     *
     * @param owner           The player's name which is owner of the ChatListener
     * @param timeout         The timeout of the ChatListener
     * @param inputRunnable   The runnable to run when the ChatListener receives input
     * @param timeoutRunnable The runnable to run when the ChatListener times out
     * @param messages        The messages to send to the player
     */
    private BlobChatListener(String owner, long timeout, Runnable inputRunnable,
                             Runnable timeoutRunnable, List<BlobMessage> messages) {
        super(owner, timeout, inputRunnable, timeoutRunnable);
        this.messages = messages;
    }

    /**
     * Runs the ChatListener
     */
    @Override
    public void runTasks() {
        super.runTasks();
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
