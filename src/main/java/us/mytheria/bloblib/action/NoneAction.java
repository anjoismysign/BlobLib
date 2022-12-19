package us.mytheria.bloblib.action;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

public class NoneAction<T extends Entity> extends Action<T> {

    private NoneAction() {
        this.actionType = ActionType.NONE;
    }

    @Override
    protected void run() {
    }

    public void updateActor(T actor) {
    }

    @Override
    public void save(ConfigurationSection section) {
        section.set("Type", "None");
    }
}
