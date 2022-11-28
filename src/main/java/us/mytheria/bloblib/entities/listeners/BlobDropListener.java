package us.mytheria.bloblib.entities.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.message.BlobMessage;

import java.util.List;

public class BlobDropListener extends DropListener {
    private List<BlobMessage> messages;
    private BukkitTask messageTask;

    public static BlobDropListener build(Player owner, Runnable inputRunnable, List<BlobMessage> messages) {
        return new BlobDropListener(owner.getName(), inputRunnable, messages);
    }

    private BlobDropListener(String owner, Runnable inputRunnable,
                             List<BlobMessage> messages) {
        super(owner, inputRunnable);
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
        messageTask.cancel();
    }

    public List<BlobMessage> getMessages() {
        return messages;
    }


}