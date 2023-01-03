package us.mytheria.bloblib.entities.manager;

import org.bukkit.plugin.Plugin;

public abstract class Manager {
    private Plugin plugin;
    private ManagerDirector managerDirector;

    public Manager(ManagerDirector managerDirector) {
        this.managerDirector = managerDirector;
        this.plugin = managerDirector.getPlugin();
        loadInConstructor();
    }

    public void loadInConstructor() {
    }

    public void loadManually() {
    }

    public void reload() {
    }

    public void unload() {
    }

    public void postWorld() {
    }

    public ManagerDirector getManagerDirector() {
        return managerDirector;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
