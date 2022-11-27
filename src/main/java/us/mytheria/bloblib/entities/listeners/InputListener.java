package us.mytheria.bloblib.entities.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public abstract class InputListener {
    protected final String owner;
    protected final Runnable inputRunnable;

    public InputListener(String owner, Runnable inputRunnable) {
        this.owner = owner;
        this.inputRunnable = inputRunnable;
    }

    public void runTasks() {
    }

    public void cancel() {
    }

    public String getOwner() {
        return owner;
    }

    public Object getInput() {
        return null;
    }

    public Runnable getInputRunnable() {
        return inputRunnable;
    }

    @Nullable
    public Player getPlayerOwner() {
        return Bukkit.getServer().getPlayer(owner);
    }
}