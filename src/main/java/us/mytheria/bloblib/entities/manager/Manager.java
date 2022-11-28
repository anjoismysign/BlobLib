package us.mytheria.bloblib.entities.manager;

public abstract class Manager {
    private ManagerDirector managerDirector;

    public Manager(ManagerDirector managerDirector) {
        this.managerDirector = managerDirector;
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
}
