package us.mytheria.bloblib.entities.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.message.BlobMessage;
import us.mytheria.bloblib.managers.SelPosListenerManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BlobSelPosListener extends SelPosListener {
    private List<BlobMessage> messages;
    private BukkitTask messageTask;

    /**
     * Will run a SelPosListener which will send messages to player every 10 ticks asynchronously
     *
     * @param owner           The owner of the SelPosListener
     * @param timeout         The timeout of the SelPosListener
     * @param inputRunnable   The runnable to run when the SelPosListener receives input
     * @param timeoutRunnable The runnable to run when the SelPosListener times out
     * @param messages        The messages to send to the player
     */
    public static BlobSelPosListener build(Player owner, long timeout, Runnable inputRunnable,
                                           Runnable timeoutRunnable, List<BlobMessage> messages) {
        return new BlobSelPosListener(owner.getName(), timeout, inputRunnable, timeoutRunnable, messages);
    }

    public static BlobSelPosListener smart(Player player, long timeout, Consumer<Block> consumer,
                                           String timeoutMessageKey, String timerMessageKey) {
        BlobLib main = BlobLib.getInstance();
        SelPosListenerManager selPosManager = main.getPositionManager();
        Optional<BlobMessage> timeoutMessage = Optional.ofNullable(BlobLibAssetAPI.getMessage(timeoutMessageKey));
        Optional<BlobMessage> timerMessage = Optional.ofNullable(BlobLibAssetAPI.getMessage(timerMessageKey));
        List<BlobMessage> messages = timerMessage.map(Collections::singletonList).orElse(Collections.emptyList());
        return new BlobSelPosListener(player.getName(), timeout,
                () -> {
                    Block input = selPosManager.getInput(player);
                    selPosManager.removePositionListener(player);
                    Bukkit.getScheduler().runTask(main, () -> {
                        if (player == null || !player.isOnline()) {
                            return;
                        }
                        consumer.accept(input);
                    });
                },
                () -> {
                    selPosManager.removePositionListener(player);
                    timeoutMessage.ifPresent(message -> message.send(player));
                }, messages);
    }

    /**
     * Will run a SelPosListener which will send messages to player every 10 ticks asynchronously
     *
     * @param owner           The player's name which is owner of the SelPosListener
     * @param timeout         The timeout of the SelPosListener
     * @param inputRunnable   The runnable to run when the SelPosListener receives input
     * @param timeoutRunnable The runnable to run when the SelPosListener times out
     * @param messages        The messages to send to the player
     */
    private BlobSelPosListener(String owner, long timeout, Runnable inputRunnable,
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

    public List<BlobMessage> getMessages() {
        return messages;
    }
}
