package us.mytheria.bloblib.action;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class Action<T extends Entity> {
    public static Action<Entity> fromConfigurationSection(ConfigurationSection section) {
        String type = Objects.requireNonNull(section.getString("Type"), "Action.Type is null");
        switch (type) {
            case "ActorCommand", "ConsoleCommand" -> {
                String command = Objects.requireNonNull(section.getString("Command"), "Action.Command is null");
                if (type.equals("ConsoleCommand"))
                    return ConsoleCommandAction.build(command);
                return CommandAction.build(command);
            }
            default -> throw new IllegalArgumentException("Unknown Action Type: " + type);
        }
    }

    private T actor;

    protected abstract void run();

    protected void updateActor(T actor) {
        this.actor = actor;
    }

    public void perform(@Nullable T entity) {
        if (updatesActor()) {
            updateActor(entity);
        }
        run();
    }

    public void perform() {
        perform(null);
    }

    public abstract void save(ConfigurationSection section);

    public T getActor() {
        return actor;
    }

    public boolean updatesActor() {
        return true;
    }

    public boolean isAsync() {
        return true;
    }
}
