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
    protected void run() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public ConsoleCommandAction<T> updateActor(T actor) {
        if (actor != null) {
            return modify(command -> command.replace("%actor%", actor.getName()));
        } else {
            return this;
        }
    }

    @Override
    public void save(ConfigurationSection section) {
        section.set("Command", command);
        section.set("Type", "ConsoleCommand");
    }

    @Override
    public ConsoleCommandAction<T> modify(Function<String, String> modifier) {
        String newCommand = modifier.apply(command);
        return new ConsoleCommandAction<>(newCommand);
    }
}
