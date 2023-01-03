package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.Tuple2;
import us.mytheria.bloblib.entities.manager.Manager;
import us.mytheria.bloblib.entities.manager.ManagerDirector;

import java.io.File;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ObjectDirector<T> extends Manager {
    private final ObjectBuilderManager<T> objectBuilderManager;
    private final ObjectManager<T> objectManager;

    public ObjectDirector(ManagerDirector managerDirector,
                          Supplier<String> titleSupplier,
                          String loadFilesPathKey,
                          Function<File, Tuple2<T, String>> readFunction) {
        super(managerDirector);
        this.objectBuilderManager = new ObjectBuilderManager<T>(managerDirector, titleSupplier) {
            @Override
            public void update() {
                this.title = getTitleSupplier().get();
            }
        };
        Optional<File> loadFilesPath = managerDirector.getFileManager().searchFile(loadFilesPathKey);
        if (loadFilesPath.isEmpty())
            throw new IllegalArgumentException("The loadFilesPathKey is not valid");
        this.objectManager = new ObjectManager<T>(managerDirector, loadFilesPath.get()) {
            @Override
            public void loadFiles(File path) {
                File[] listOfFiles = path.listFiles();
                for (File file : listOfFiles) {
                    if (file.getName().equals(".DS_Store"))
                        continue;
                    if (file.isFile()) {
                        Tuple2<T, String> tuple = readFunction.apply(file);
                        objectManager.addObject(tuple.second(), tuple.first());
                    }
                    if (file.isDirectory())
                        loadFiles(file);
                }
            }
        };
    }

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
