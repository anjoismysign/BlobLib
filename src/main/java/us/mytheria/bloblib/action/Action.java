package us.mytheria.bloblib.action;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;
import java.util.Objects;

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
        switch (type) {
            case "ActorCommand", "ConsoleCommand" -> {
                String command = Objects.requireNonNull(section.getString("Command"), "Action.Command is null");
                if (type.equals("ConsoleCommand"))
                    return ConsoleCommandAction.build(command);
                return CommandAction.build(command);
            }
            case "None" -> {
                return new NoneAction<>();
            }
            default -> throw new IllegalArgumentException("Unknown Action Type: " + type);
        }
    }

    private T actor;
    /**
     * The type of action
     */
    protected ActionType actionType;

    /**
     * Runs the action.
     * Its implementation is left to the child class.
     */
    protected abstract void run();

    /**
     * Updates the actor
     *
     * @param actor The actor to update
     */
    protected void updateActor(T actor) {
        this.actor = actor;
    }

    /**
     * Performs the action
     *
     * @param entity The entity to perform the action on
     */
    public void perform(@Nullable T entity) {
        if (updatesActor()) {
            updateActor(entity);
        }
        run();
    }

    /**
     * Performs the action
     */
    public void perform() {
        perform(null);
    }

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
     * @return If the action updates the actor
     */
    public boolean updatesActor() {
        return true;
    }

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
}
