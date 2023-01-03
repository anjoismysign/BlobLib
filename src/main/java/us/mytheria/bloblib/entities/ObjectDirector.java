package us.mytheria.bloblib.entities;

import us.mytheria.bloblib.entities.manager.Manager;
import us.mytheria.bloblib.entities.manager.ManagerDirector;

import java.util.function.Supplier;

public class ObjectDirector<T> extends Manager {
    private final ObjectBuilderManager<T> objectBuilderManager;
    private final ObjectManager<T> objectManager;

    public ObjectDirector(ManagerDirector managerDirector,
                          Supplier<String> titleSupplier,
                          ObjectManager<T> objectManager) {
        super(managerDirector);
        this.objectBuilderManager = new ObjectBuilderManager<T>(managerDirector, titleSupplier) {
            @Override
            public void update() {
                this.title = getTitleSupplier().get();
            }
        };
        this.objectManager = objectManager;
    }
}
