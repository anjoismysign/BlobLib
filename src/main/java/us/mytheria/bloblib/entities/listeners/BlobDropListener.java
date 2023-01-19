package us.mytheria.bloblib.entities.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.BlobLibAPI;
import us.mytheria.bloblib.entities.message.SerialBlobMessage;
import us.mytheria.bloblib.managers.DropListenerManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BlobDropListener extends DropListener {
    private List<SerialBlobMessage> messages;
    private BukkitTask messageTask;

    public static BlobDropListener build(Player owner, Runnable inputRunnable, List<SerialBlobMessage> messages) {
        return new BlobDropListener(owner.getName(), inputRunnable, messages);
    }

    public static BlobDropListener smart(Player owner, Consumer<ItemStack> consumer, String timerMessageKey) {
        BlobLib main = BlobLib.getInstance();
        DropListenerManager dropManager = main.getDropListenerManager();
        Optional<SerialBlobMessage> timerMessage = Optional.ofNullable(BlobLibAPI.getMessage(timerMessageKey));
        List<SerialBlobMessage> messages = timerMessage.map(Collections::singletonList).orElse(Collections.emptyList());
        return new BlobDropListener(owner.getName(), () -> {
            ItemStack input = dropManager.getInput(owner);
            dropManager.removeDropListener(owner);
            Bukkit.getScheduler().runTask(main, () -> {
                if (owner == null || !owner.isOnline()) {
                    return;
                }
                consumer.accept(input);
            });
        }, messages);
    }

    private BlobDropListener(String owner, Runnable inputRunnable,
                             List<SerialBlobMessage> messages) {
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
                messages.forEach(message -> message.sendAndPlay(player));
            }
        };
        this.messageTask = bukkitRunnable.runTaskTimerAsynchronously(BlobLib.getInstance(), 0, 10);
    }

    @Override
    public void cancel() {
        messageTask.cancel();
    }

    public List<SerialBlobMessage> getMessages() {
        return messages;
    }


}