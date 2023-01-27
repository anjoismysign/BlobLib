package us.mytheria.bloblib.action;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.Objects;

/**
 * @param <T> The type of entity this action is for
 * @author anjoismysign
 * <p>
 * CommandAction is an action in which the Actor is
 * an Entity and the command is executed by the
 * Entity.
 */
public class CommandAction<T extends Entity> extends Action<T> {
    private String command;

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
     * Updates the actor
     *
     * @param actor The actor to update to
     */
    public void updateActor(T actor) {
        super.updateActor(actor);
        if (actor != null)
            command = command.replace("%actor%", actor.getName());
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
}
