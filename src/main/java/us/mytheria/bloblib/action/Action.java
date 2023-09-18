package us.mytheria.bloblib.action;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.Objects;
import java.util.function.Function;

/**
 * An action that can be performed on an entity
 *
 * @param <T> The type of entity to perform the action on
 */
public abstract class Action<T extends Entity> {
    /**
     * Creates an action from a configuration section
     *
     * @param section The section to create the action from
     * @return The action
     */
    public static Action<Entity> fromConfigurationSection(ConfigurationSection section) {
        String type = Objects.requireNonNull(section.getString("Type"), "Action.Type is null");
        ActionType actionType;
        try {
            actionType = ActionType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown Action Type: " + type);
        }
        switch (actionType) {
            case ACTOR_COMMAND -> {
                String command = Objects.requireNonNull(section.getString("Command"), "Action.Command is null");
                return CommandAction.build(command);
            }
            case CONSOLE_COMMAND -> {
                String command = Objects.requireNonNull(section.getString("Command"), "Action.Command is null");
                return ConsoleCommandAction.build(command);
            }
            default -> throw new IllegalArgumentException("Unknown Action Type: " + type);
        }
    }

    protected T actor;
    /**
     * The type of action
     */
    protected ActionType actionType;

    /**
     * Runs the action.
     * Its implementation is left to the child class.
     */
    public abstract void run();

    /**
     * Updates the actor and runs the action.
     * A quick way to perform an action on an actor.
     *
     * @param actor The actor to update
     * @param <U>   The type of entity to perform the action on
     */
    public <U extends Entity> void perform(U actor) {
        if (updatesActor()) {
            Action<U> updatedAction = updateActor(actor);
            updatedAction.run();
        } else {
            run();
        }
    }

    /**
     * Modifies the action, updates the actor and runs the action.
     * Will modify the action before performing it.
     * A quick way to perform an action on an actor.
     *
     * @param actor    The actor to update
     * @param function The function to modify the action
     * @param <U>      The type of entity to perform the action on
     */
    public <U extends Entity> void perform(U actor, Function<String, String> function) {
        Action<T> modify = modify(function);
        if (updatesActor()) {
            Action<U> updatedAction = modify.updateActor(actor);
            updatedAction.run();
        } else {
            modify.run();
        }
    }

    /**
     * Updates the actor
     *
     * @param actor The actor to update
     */
    public abstract <U extends Entity> Action<U> updateActor(U actor);

    /**
     * Saves the action to a configuration section
     *
     * @param section The section to save to
     */
    public abstract void save(ConfigurationSection section);

    /**
     * @return The actor
     */
    public T getActor() {
        return actor;
    }

    /**
     * @return If the action updates the actor whenever it is performed
     */
    public abstract boolean updatesActor();

    /**
     * @return If the action is asynchronous
     */
    public boolean isAsync() {
        return true;
    }

    /**
     * @return The type of action
     */
    public ActionType getActionType() {
        return actionType;
    }

    public abstract Action<T> modify(Function<String, String> modifier);
}