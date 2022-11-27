package us.mytheria.bloblib.entities.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.mytheria.bloblib.BlobLib;

import javax.annotation.Nullable;

public abstract class InputListener {
    protected final String owner;
    protected final long timeout;
    protected final Runnable inputRunnable;
    protected final Runnable timeoutRunnable;
    protected BukkitTask task;

    public InputListener(String owner, long timeout, Runnable inputRunnable,
                         Runnable timeoutRunnable) {
        this.owner = owner;
        this.timeout = timeout;
        this.inputRunnable = inputRunnable;
        this.timeoutRunnable = timeoutRunnable;
    }

    public void runTasks() {
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                InputListener.this.cancel();
                timeoutRunnable.run();
            }
        };
        this.task = bukkitRunnable.runTaskLater(BlobLib.getInstance(), timeout);
    }

    public void cancel() {
        task.cancel();
    }

    public String getOwner() {
        return owner;
    }

    public Object getInput() {
        return null;
    }

    public long getTimeout() {
        return timeout;
    }

    public BukkitTask getTask() {
        return task;
    }

    public Runnable getInputRunnable() {
        return inputRunnable;
    }

    public Runnable getTimeoutRunnable() {
        return timeoutRunnable;
    }

    @Nullable
    public Player getPlayerOwner() {
        return Bukkit.getServer().getPlayer(owner);
    }
}