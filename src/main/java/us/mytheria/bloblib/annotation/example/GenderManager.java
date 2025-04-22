package us.mytheria.bloblib.annotation.example;


import us.mytheria.bloblib.annotation.BManager;
import us.mytheria.bloblib.managers.Manager;
import us.mytheria.bloblib.managers.ManagerDirector;

@BManager
public class GenderManager extends Manager {

    public GenderManager(ManagerDirector director) {
        super(director);
    }

    @Override
    public void reload() {
        System.out.println("GenderManager is reloading!");
    }
}
