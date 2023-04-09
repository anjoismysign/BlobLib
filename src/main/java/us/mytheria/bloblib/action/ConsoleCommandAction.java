package us.mytheria.bloblib.action;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.Objects;
import java.util.function.Function;

/**
 * @param <T> The type of entity this action is for
 * @author anjoismysign
 */
public class ConsoleCommandAction<T extends Entity> extends Action<T> {
    private final String command;

    public static <T extends Entity> ConsoleCommandAction<T> build(String command) {
        Objects.requireNonNull(command);
        return new <T>ConsoleCommandAction<T>(command);
    }

    private ConsoleCommandAction(String command) {
        this.actionType = ActionType.CONSOLE_COMMAND;
        this.command = command;
    }

    @Override
    public void run() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    /**
     * Updates the actor.
     * This will return a new instance of the action.
     *
     * @param actor The actor to update to
     */
    @Override
    public <U extends Entity> ConsoleCommandAction<U> updateActor(U actor) {
        if (actor != null) {
            String updatedCommand = command.replace("%actor%", actor.getName());
            ConsoleCommandAction<U> updatedAction = new ConsoleCommandAction<>(updatedCommand);
            updatedAction.actor = actor;
            return updatedAction;
        } else {
            throw new IllegalArgumentException("Actor cannot be null");
        }
    }

    @Override
    public void save(ConfigurationSection section) {
        section.set("Command", command);
        section.set("Type", "ConsoleCommand");
    }

    /**
     * Modifies the command.
     * This will return a new instance of the action.
     *
     * @param modifier The modifier to use
     * @return The new CommandAction
     */
    @Override
    public ConsoleCommandAction<T> modify(Function<String, String> modifier) {
        String newCommand = modifier.apply(command);
        return new ConsoleCommandAction<>(newCommand);
    }


    @Override
    public boolean updatesActor() {
        return true;
    }
}
