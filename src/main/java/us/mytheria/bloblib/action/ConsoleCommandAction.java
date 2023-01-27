package us.mytheria.bloblib.action;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.Objects;

/**
 * @param <T> The type of entity this action is for
 * @author anjoismysign
 */
public class ConsoleCommandAction<T extends Entity> extends Action<T> {
    private String command;

    public static <T extends Entity> ConsoleCommandAction<T> build(String command) {
        Objects.requireNonNull(command);
        return new <T>ConsoleCommandAction<T>(command);
    }

    private ConsoleCommandAction(String command) {
        this.actionType = ActionType.CONSOLE_COMMAND;
        this.command = command;
    }

    @Override
    protected void run() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public void updateActor(T actor) {
        super.updateActor(actor);
        if (actor != null)
            command = command.replace("%actor%", actor.getName());
    }

    @Override
    public void save(ConfigurationSection section) {
        section.set("Command", command);
        section.set("Type", "ConsoleCommand");
    }
}
