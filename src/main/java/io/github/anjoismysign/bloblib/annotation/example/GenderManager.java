package io.github.anjoismysign.bloblib.annotation.example;


import io.github.anjoismysign.bloblib.annotation.BManager;
import io.github.anjoismysign.bloblib.managers.Manager;
import io.github.anjoismysign.bloblib.managers.ManagerDirector;

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
