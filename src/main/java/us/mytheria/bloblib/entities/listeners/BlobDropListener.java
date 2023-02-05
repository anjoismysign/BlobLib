package us.mytheria.bloblib.entities.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.message.BlobMessage;
import us.mytheria.bloblib.managers.DropListenerManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author anjoismysign
 * A listener that runs a task when the owner drops an item.
 */
public class BlobDropListener extends DropListener {
    private final List<BlobMessage> messages;
    private BukkitTask messageTask;

    /**
     * Creates a new DropListener.
     *
     * @param owner         the owner of the listener
     * @param inputRunnable the runnable to run when the input is received
     * @param messages      the messages to send to the owner
     * @return the new DropListener
     * @deprecated use {@link #smart(Player, Consumer, String)} instead
     */
    @Deprecated
    public static BlobDropListener build(Player owner, Runnable inputRunnable,
                                         List<BlobMessage> messages) {
        return new BlobDropListener(owner.getName(), inputListener -> inputRunnable.run(), messages);
    }

    /**
     * Creates a new DropListener. Smart instance.
     *
     * @param owner           the owner of the listener
     * @param consumer        the consumer of the input
     * @param timerMessageKey the key of the message to send to the owner
     * @return the new DropListener
     */
    public static BlobDropListener smart(Player owner, Consumer<ItemStack> consumer,
                                         String timerMessageKey) {
        BlobLib main = BlobLib.getInstance();
        DropListenerManager dropManager = main.getDropListenerManager();
        Optional<BlobMessage> timerMessage = Optional.ofNullable(BlobLibAssetAPI.getMessage(timerMessageKey));
        List<BlobMessage> messages = timerMessage.map(Collections::singletonList).orElse(Collections.emptyList());
        return new BlobDropListener(owner.getName(), listener -> {
            ItemStack input = listener.getInput();
            dropManager.removeDropListener(owner);
            Bukkit.getScheduler().runTask(main, () -> {
                if (owner == null || !owner.isOnline()) {
                    return;
                }
                consumer.accept(input);
            });
        }, messages);
    }

    /**
     * Creates a new DropListener
     *
     * @param owner         the owner of the listener
     * @param inputConsumer the consumer to run when the input is received
     */
    private BlobDropListener(String owner, Consumer<BlobDropListener> inputConsumer,
                             List<BlobMessage> messages) {
        super(owner, inputListener ->
                inputConsumer.accept((BlobDropListener) inputListener));
        this.messages = messages;
    }

    /**
     * Runs the tasks
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
     * Cancels the task
     */
    @Override
    public void cancel() {
        messageTask.cancel();
    }

    /**
     * @return the messages
     */
    public List<BlobMessage> getMessages() {
        return messages;
    }

}