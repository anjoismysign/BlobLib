package us.mytheria.bloblib.entities.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.inventory.VariableSelector;
import us.mytheria.bloblib.entities.message.BlobMessage;

import java.util.List;

public class BlobSelectorListener<T> extends SelectorListener<T> {
    private final List<BlobMessage> messages;
    private BukkitTask messageTask;

    /**
     * Will run a SelectorListener which will send messages to player every 10 ticks asynchronously
     *
     * @param owner         The owner of the SelectorListener
     * @param inputRunnable The runnable to run when the SelectorListener receives input
     * @param messages      The messages to send to the player
     */
    public static <T> BlobSelectorListener<T> build(Player owner, Runnable inputRunnable
            , List<BlobMessage> messages, VariableSelector<T> selector) {
        return new BlobSelectorListener<>(owner.getName(),
                inputRunnable, messages, selector);
    }

    /**
     * Will run a SelectorListener which will send messages to player every 10 ticks asynchronously
     *
     * @param owner         The player's name which is owner of the SelectorListener
     * @param inputRunnable The runnable to run when the SelectorListener receives input
     * @param messages      The messages to send to the player
     */
    private BlobSelectorListener(String owner, Runnable inputRunnable, List<BlobMessage> messages,
                                 VariableSelector<T> selector) {
        super(owner, inputRunnable, selector);
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
                messages.forEach(message -> message.send(player));
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
