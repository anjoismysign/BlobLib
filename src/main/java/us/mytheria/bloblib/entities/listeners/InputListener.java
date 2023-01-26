package us.mytheria.bloblib.entities.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * @author anjoismysign
 * A listener that runs tasks when input is received
 */
public abstract class InputListener {
    protected final String owner;
    protected final Runnable inputRunnable;

    /**
     * Creates a new InputListener
     *
     * @param owner         the owner of this listener
     * @param inputRunnable the runnable that will be executed when the input is received
     */
    public InputListener(String owner, Runnable inputRunnable) {
        this.owner = owner;
        this.inputRunnable = inputRunnable;
    }

    /**
     * Runs the tasks
     */
    public void runTasks() {
    }

    /**
     * Cancels the listener
     */
    public void cancel() {
    }

    /**
     * @return the owner of this listener
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @return the input that will be received
     */
    public Object getInput() {
        return null;
    }

    /**
     * @return the runnable that will be executed when the input is received
     */
    public Runnable getInputRunnable() {
        return inputRunnable;
    }

    /**
     * @return the player who owns this listener
     */
    @Nullable
    public Player getPlayerOwner() {
        return Bukkit.getServer().getPlayer(owner);
    }
}