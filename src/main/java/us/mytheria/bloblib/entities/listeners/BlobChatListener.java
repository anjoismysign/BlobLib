package us.mytheria.bloblib.entities.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.BlobLibAPI;
import us.mytheria.bloblib.entities.message.SerialBlobMessage;
import us.mytheria.bloblib.managers.ChatListenerManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BlobChatListener extends ChatListener {
    private List<SerialBlobMessage> messages;
    private BukkitTask messageTask;

    /**
     * Will run a ChatListener which will send messages to player every 10 ticks asynchronously
     *
     * @param owner           The owner of the ChatListener
     * @param timeout         The timeout of the ChatListener
     * @param inputRunnable   The runnable to run when the ChatListener receives input
     * @param timeoutRunnable The runnable to run when the ChatListener times out
     * @param messages        The messages to send to the player
     */
    public static BlobChatListener build(Player owner, long timeout, Runnable inputRunnable,
                                         Runnable timeoutRunnable, List<SerialBlobMessage> messages) {
        return new BlobChatListener(owner.getName(), timeout, inputRunnable, timeoutRunnable, messages);
    }

    public static BlobChatListener smart(Player owner, long timeout, Consumer<String> consumer,
                                         String timeoutMessageKey, String timerMessageKey) {
        BlobLib main = BlobLib.getInstance();
        ChatListenerManager chatManager = main.getChatManager();
        Optional<SerialBlobMessage> timeoutMessage = Optional.ofNullable(BlobLibAPI.getMessage(timeoutMessageKey));
        Optional<SerialBlobMessage> timerMessage = Optional.ofNullable(BlobLibAPI.getMessage(timerMessageKey));
        List<SerialBlobMessage> messages = timerMessage.map(Collections::singletonList).orElse(Collections.emptyList());
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
                             Runnable timeoutRunnable, List<SerialBlobMessage> messages) {
        super(owner, timeout, inputRunnable, timeoutRunnable);
        this.messages = messages;
    }

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

    @Override
    public void cancel() {
        getTask().cancel();
        messageTask.cancel();
    }

    public List<SerialBlobMessage> getMessages() {
        return messages;
    }
}
