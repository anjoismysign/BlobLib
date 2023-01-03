package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.Tuple2;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import us.mytheria.bloblib.entities.inventory.ObjectBuilder;
import us.mytheria.bloblib.entities.manager.Manager;
import us.mytheria.bloblib.entities.manager.ManagerDirector;

import java.io.File;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class ObjectDirector<T> extends Manager implements Listener {
    private final ObjectBuilderManager<T> objectBuilderManager;
    private final ObjectManager<T> objectManager;

    public ObjectDirector(ManagerDirector managerDirector,
                          Supplier<String> titleSupplier,
                          Function<UUID, ObjectBuilder<T>> builderFunction,
                          String loadFilesPathKey,
                          Function<File, Tuple2<T, String>> readFunction) {
        super(managerDirector);
        this.objectBuilderManager = new ObjectBuilderManager<T>(managerDirector,
                titleSupplier, builderFunction) {
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
                          Function<UUID, ObjectBuilder<T>> builderFunction,
                          ObjectManager<T> objectManager) {
        super(managerDirector);
        this.objectBuilderManager = new ObjectBuilderManager<T>(managerDirector,
                titleSupplier, builderFunction) {
            @Override
            public void update() {
                this.title = getTitleSupplier().get();
            }
        };
        this.objectManager = objectManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String invname = e.getView().getTitle();
        if (!invname.equals(objectBuilderManager.title)) {
            return;
        }
        int slot = e.getRawSlot();
        Player player = (Player) e.getWhoClicked();
        ObjectBuilder<T> builder = objectBuilderManager.getOrDefault(player.getUniqueId());
        if (slot >= builder.getSize()) {
            return;
        }
        e.setCancelled(true);
        builder.handle(slot, player);
    }
}
