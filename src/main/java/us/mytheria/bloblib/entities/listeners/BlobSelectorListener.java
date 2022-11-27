package us.mytheria.bloblib.entities.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.inventory.VariableSelector;
import us.mytheria.bloblib.entities.message.BlobMessage;

import java.util.List;

public class BlobSelectorListener extends SelectorListener {
    private List<BlobMessage> messages;
    private BukkitTask messageTask;

    /**
     * Will run a SelectorListener which will send messages to player every 10 ticks asynchronously
     *
     * @param owner           The owner of the SelectorListener
     * @param timeout         The timeout of the SelectorListener
     * @param inputRunnable   The runnable to run when the SelectorListener receives input
     * @param timeoutRunnable The runnable to run when the SelectorListener times out
     * @param messages        The messages to send to the player
     */
    public static BlobSelectorListener build(Player owner, long timeout, Runnable inputRunnable,
                                             Runnable timeoutRunnable, List<BlobMessage> messages,
                                             VariableSelector selector) {
        return new BlobSelectorListener(owner.getName(), timeout,
                inputRunnable, timeoutRunnable, messages, selector);
    }

    /**
     * Will run a SelectorListener which will send messages to player every 10 ticks asynchronously
     *
     * @param owner           The player's name which is owner of the SelectorListener
     * @param timeout         The timeout of the SelectorListener
     * @param inputRunnable   The runnable to run when the SelectorListener receives input
     * @param timeoutRunnable The runnable to run when the SelectorListener times out
     * @param messages        The messages to send to the player
     */
    private BlobSelectorListener(String owner, long timeout, Runnable inputRunnable,
                                 Runnable timeoutRunnable, List<BlobMessage> messages,
                                 VariableSelector selector) {
        super(owner, timeout, inputRunnable, timeoutRunnable, selector);
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
        this.messageTask = bukkitRunnable.runTaskTimerAsynchronously(BlobLib.getInstance(), 0, 10);
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
