package us.mytheria.bloblib.action;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

public abstract class Action<T extends Entity> {
    private T actor;

    protected abstract void run();

    protected void updateActor(T actor) {
        this.actor = actor;
    }

    public void perform(@Nullable T entity) {
        if (updatesActor()) {
            updateActor(entity);
            return;
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
