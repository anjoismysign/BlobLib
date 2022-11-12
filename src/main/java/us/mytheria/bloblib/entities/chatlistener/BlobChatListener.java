package us.mytheria.bloblib.entities.chatlistener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.message.BlobMessage;

import java.util.List;

public class BlobChatListener extends ChatListener {
    private List<BlobMessage> messages;
    private BukkitTask messageTask;

    /**
     * Will run a ChatListener which will send messages to player each second asynchronously
     *
     * @param owner           The owner of the ChatListener
     * @param timeout         The timeout of the ChatListener
     * @param inputRunnable   The runnable to run when the ChatListener receives input
     * @param timeoutRunnable The runnable to run when the ChatListener times out
     * @param messages        The messages to send to the player
     */
    public static BlobChatListener build(Player owner, long timeout, Runnable inputRunnable,
                                         Runnable timeoutRunnable, List<BlobMessage> messages) {
        return new BlobChatListener(owner.getName(), timeout, inputRunnable, timeoutRunnable, messages);
    }

    /**
     * Will run a ChatListener which will send messages to player each second asynchronously
     *
     * @param owner           The player's name which is owner of the ChatListener
     * @param timeout         The timeout of the ChatListener
     * @param inputRunnable   The runnable to run when the ChatListener receives input
     * @param timeoutRunnable The runnable to run when the ChatListener times out
     * @param messages        The messages to send to the player
     */
    public BlobChatListener(String owner, long timeout, Runnable inputRunnable,
                            Runnable timeoutRunnable, List<BlobMessage> messages) {
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
                messages.forEach(message -> message.send(player));
            }
        };
        this.messageTask = bukkitRunnable.runTaskTimerAsynchronously(BlobLib.getInstance(), 0, 20);
    }

    @Override
    public void cancel() {
        getTask().cancel();
        messageTask.cancel();
    }

    public List<BlobMessage> getMessages() {
        return messages;
    }
}
