package us.mytheria.bloblib.action;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.Objects;

public class CommandAction<T extends Entity> extends Action<T> {
    private String command;

    public static <T extends Entity> CommandAction<T> build(String command) {
        Objects.requireNonNull(command);
        return new <T>CommandAction<T>(command);
    }

    private CommandAction(String command) {
        this.actionType = ActionType.ACTOR_COMMAND;
        this.command = command;
    }

    @Override
    protected void run() {
        Bukkit.dispatchCommand(getActor(), command);
    }

    public void updateActor(T actor) {
        super.updateActor(actor);
        if (actor != null)
            command = command.replace("%actor%", actor.getName());
    }

    @Override
    public void save(ConfigurationSection section) {
        section.set("Command", command);
        section.set("Type", "ActorCommand");
    }
}
