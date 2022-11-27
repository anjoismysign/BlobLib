package us.mytheria.bloblib.entities.listeners;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.mytheria.bloblib.BlobLib;

public abstract class TimeoutInputListener extends InputListener {
    protected final long timeout;
    protected final Runnable timeoutRunnable;
    protected BukkitTask task;

    public TimeoutInputListener(String owner, long timeout, Runnable inputRunnable,
                                Runnable timeoutRunnable) {
        super(owner, inputRunnable);
        this.timeout = timeout;
        this.timeoutRunnable = timeoutRunnable;
    }

    @Override
    public void runTasks() {
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                TimeoutInputListener.this.cancel();
                timeoutRunnable.run();
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

    public Runnable getTimeoutRunnable() {
        return timeoutRunnable;
    }
}