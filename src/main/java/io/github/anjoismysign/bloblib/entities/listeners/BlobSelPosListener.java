package io.github.anjoismysign.bloblib.entities.listeners;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.api.BlobLibMessageAPI;
import io.github.anjoismysign.bloblib.entities.message.BlobMessage;
import io.github.anjoismysign.bloblib.managers.SelPosListenerManager;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class BlobSelPosListener extends SelPosListener {
    private final List<BlobMessage> messages;
    private BukkitTask messageTask;

    /**
     * Will run a SelPosListener which will send messages to player every 10 ticks asynchronously
     *
     * @param owner           The owner of the SelPosListener
     * @param timeout         The timeout of the SelPosListener
     * @param inputConsumer   The consumer to run when the SelPosListener receives input
     * @param timeoutConsumer The consumer to run when the SelPosListener times out
     * @param messages        The messages to send to the player
     * @deprecated Use {@link #smart(Player, long, Consumer, String, String)} instead
     */
    @Deprecated
    public static BlobSelPosListener build(Player owner, long timeout, Consumer<BlobSelPosListener> inputConsumer,
                                           Consumer<BlobSelPosListener> timeoutConsumer, List<BlobMessage> messages) {
        return new BlobSelPosListener(owner.getName(), timeout, inputConsumer, timeoutConsumer, messages);
    }

    /**
     * Will run a SelPosListener which will send messages to player every 10 ticks asynchronously
     *
     * @param player            The player to send the messages to
     * @param timeout           The timeout of the SelPosListener
     * @param consumer          The consumer to run when the SelPosListener receives input
     * @param timeoutMessageKey The message to send to the player when the SelPosListener times out
     * @param timerMessageKey   The message to send to the player every 10 ticks
     * @return The SelPosListener
     */
    public static BlobSelPosListener smart(Player player, long timeout, Consumer<Block> consumer,
                                           String timeoutMessageKey, String timerMessageKey) {
        BlobLib main = BlobLib.getInstance();
        SelPosListenerManager selPosManager = main.getPositionManager();
        Optional<BlobMessage> timeoutMessage = Optional.ofNullable(BlobLibMessageAPI.getInstance().getMessage(timeoutMessageKey, player));
        Optional<BlobMessage> timerMessage = Optional.ofNullable(BlobLibMessageAPI.getInstance().getMessage(timerMessageKey, player));
        List<BlobMessage> messages = timerMessage.map(Collections::singletonList).orElse(Collections.emptyList());
        UUID uuid = player.getUniqueId();
        return new BlobSelPosListener(player.getName(), timeout,
                inputListener -> {
                    Block input = inputListener.getInput();
                    selPosManager.removePositionListener(player);
                    Bukkit.getScheduler().runTask(main, () -> {
                        if (player != Bukkit.getPlayer(uuid))
                            return;
                        consumer.accept(input);
                    });
                },
                timeoutListener -> {
                    selPosManager.removePositionListener(player);
                    timeoutMessage.ifPresent(message -> message.send(player));
                }, messages);
    }

    private BlobSelPosListener(String owner, long timeout, Consumer<BlobSelPosListener> inputConsumer,
                               Consumer<BlobSelPosListener> timeoutConsumer, List<BlobMessage> messages) {
        super(owner, timeout, inputListener -> inputConsumer
                        .accept((BlobSelPosListener) inputListener),
                timeoutListener -> timeoutConsumer
                        .accept((BlobSelPosListener) timeoutListener));
        this.messages = messages;
    }

    @Override
    public void runTasks() {
        super.runTasks();
        Player player = Bukkit.getPlayer(getOwner());
        UUID uuid = player.getUniqueId();
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (player != Bukkit.getPlayer(uuid)) {
                    this.cancel();
                    return;
                }
                messages.forEach(message -> message.handle(player));
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
