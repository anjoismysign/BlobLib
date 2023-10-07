package us.mytheria.bloblib.entities.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @author anjoismysign
 * A listener that runs tasks when input is received
 */
public abstract class InputListener {
    protected final String owner;
    protected final Consumer<InputListener> inputConsumer;

    /**
     * Creates a new InputListener
     *
     * @param owner         the owner of this listener
     * @param inputConsumer the consumer that will be executed when the input is received
     */
    public InputListener(String owner, Consumer<InputListener> inputConsumer) {
        this.owner = owner;
        this.inputConsumer = inputConsumer;
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
     * @return the consumer that will be executed when the input is received
     */
    public Consumer<InputListener> getInputConsumer() {
        return inputConsumer;
    }

    /**
     * @return the player who owns this listener
     */
    @Nullable
    public Player getPlayerOwner() {
        return Bukkit.getServer().getPlayer(owner);
    }
}