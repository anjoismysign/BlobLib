package io.github.anjoismysign.bloblib.action;

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

    public static <T extends Entity> ConsoleCommandAction<T> build(String command, String key) {
        Objects.requireNonNull(command);
        return new ConsoleCommandAction<>(command, key);
    }

    private ConsoleCommandAction(String command, String key) {
        this.actionType = ActionType.CONSOLE_COMMAND;
        this.command = command;
        this.key = key;
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
            ConsoleCommandAction<U> updatedAction = new ConsoleCommandAction<>(updatedCommand, key);
            updatedAction.actor = actor;
            return updatedAction;
        } else {
            if (actionType != ActionType.NO_ACTOR) {
                throw new IllegalArgumentException("Actor cannot be null");
            } else {
                return new ConsoleCommandAction<>(command, key);
            }
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
        return new ConsoleCommandAction<>(newCommand, key);
    }


    @Override
    public boolean updatesActor() {
        return true;
    }
}
