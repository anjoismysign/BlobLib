package us.mytheria.bloblib.managers;

public abstract class Manager {
    private final BlobPlugin plugin;
    private final ManagerDirector managerDirector;

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

    public BlobPlugin getPlugin() {
        return plugin;
    }
}
