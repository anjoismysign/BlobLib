package us.mytheria.bloblib.action;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.Objects;
import java.util.function.Function;

/**
 * @param <T> The type of entity this action is for
 * @author anjoismysign
 * <p>
 * CommandAction is an action in which the Actor is
 * an Entity and the command is executed by the
 * Entity.
 */
public class CommandAction<T extends Entity> extends Action<T> {
    private final String command;

    /**
     * Creates a new CommandAction
     *
     * @param command The command to execute
     * @param <T>     The entity type
     * @return The new CommandAction
     */
    public static <T extends Entity> CommandAction<T> build(String command) {
        Objects.requireNonNull(command);
        return new <T>CommandAction<T>(command);
    }

    private CommandAction(String command) {
        this.actionType = ActionType.ACTOR_COMMAND;
        this.command = command;
    }

    /**
     * Runs the command as the actor
     */
    @Override
    protected void run() {
        Bukkit.dispatchCommand(getActor(), command);
    }

    /**
     * Updates the actor.
     * This will return a new instance of the action.
     *
     * @param actor The actor to update to
     */
    @Override
    public <U extends Entity> CommandAction<U> updateActor(U actor) {
        if (actor != null) {
            String updatedCommand = command.replace("%actor%", actor.getName());
            CommandAction<U> updatedAction = new CommandAction<>(updatedCommand);
            updatedAction.actor = actor;
            return updatedAction;
        } else {
            throw new IllegalArgumentException("Actor cannot be null");
        }
    }

    /**
     * Save the action to a configuration section
     *
     * @param section The section to save to
     */
    @Override
    public void save(ConfigurationSection section) {
        section.set("Command", command);
        section.set("Type", "ActorCommand");
    }

    /**
     * Modifies the command.
     * This will return a new instance of the action.
     *
     * @param modifier The modifier to use
     * @return The new CommandAction
     */
    @Override
    public CommandAction<T> modify(Function<String, String> modifier) {
        String newCommand = modifier.apply(command);
        return new CommandAction<>(newCommand);
    }
}
