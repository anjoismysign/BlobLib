package us.mytheria.bloblib.entities.chatlistener;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.mytheria.bloblib.BlobLib;

public class ChatListener {
    private String owner;
    private String input;
    private long timeout;
    private Runnable inputRunnable;
    private Runnable timeoutRunnable;
    private BukkitTask task;

    public ChatListener(String owner, long timeout, Runnable inputRunnable,
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
                ChatListener.this.cancel();
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

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
        cancel();
        inputRunnable.run();
    }

    public long getTimeout() {
        return timeout;
    }

    public BukkitTask getTask() {
        return task;
    }
}
