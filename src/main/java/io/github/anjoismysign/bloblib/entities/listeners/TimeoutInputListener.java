package io.github.anjoismysign.bloblib.entities.listeners;

import io.github.anjoismysign.bloblib.BlobLib;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public abstract class TimeoutInputListener extends InputListener {
    protected final long timeout;
    protected final Consumer<TimeoutInputListener> timeoutConsumer;
    protected BukkitTask task;

    public TimeoutInputListener(String owner, long timeout,
                                Consumer<TimeoutInputListener> inputConsumer,
                                Consumer<TimeoutInputListener> timeoutConsumer) {
        super(owner, inputListener ->
                inputConsumer.accept((TimeoutInputListener) inputListener));
        this.timeout = timeout;
        this.timeoutConsumer = timeoutConsumer;
    }

    @Override
    public void runTasks() {
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                TimeoutInputListener.this.cancel();
                timeoutConsumer.accept(TimeoutInputListener.this);
            }
        };
        this.task = bukkitRunnable.runTaskLater(BlobLib.getInstance(), timeout);
    }

    @Override
    public void cancel() {
        task.cancel();
    }

    public long getTimeout() {
        return timeout;
    }

    public BukkitTask getTask() {
        return task;
    }

    public Consumer<TimeoutInputListener> getTimeoutConsumer() {
        return timeoutConsumer;
    }
}