package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.Tuple2;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import us.mytheria.bloblib.entities.inventory.ObjectBuilder;
import us.mytheria.bloblib.entities.manager.Manager;
import us.mytheria.bloblib.entities.manager.ManagerDirector;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

public class ObjectDirector<T> extends Manager implements Listener {
    private final ObjectBuilderManager<T> objectBuilderManager;
    private final ObjectManager<T> objectManager;
    private Consumer<InventoryClickEvent> clickEventConsumer;

    public ObjectDirector(ManagerDirector managerDirector,
                          String fileKey,
                          String loadFilesPathKey,
                          Function<File, Tuple2<T, String>> readFunction) {
        super(managerDirector);
        this.objectBuilderManager = new ObjectBuilderManager<>(managerDirector,
                fileKey);
        Optional<File> loadFilesPath = managerDirector.getFileManager().searchFile(loadFilesPathKey);
        if (loadFilesPath.isEmpty())
            throw new IllegalArgumentException("The loadFilesPathKey is not valid");
        this.objectManager = new ObjectManager<>(managerDirector, loadFilesPath.get(),
                HashMap::new) {
            @Override
            public void loadFiles(File path) {
                if (!path.exists())
                    path.mkdir();
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
        clickEventConsumer = e -> {
            Logger logger = Bukkit.getLogger();
            String invname = e.getView().getTitle();
            logger.info("Inventory clicked: " + invname);
            if (!invname.equals(objectBuilderManager.title)) {
                return;
            }
            logger.info("Inventory matches: " + invname);
            int slot = e.getRawSlot();
            Player player = (Player) e.getWhoClicked();
            ObjectBuilder<T> builder = objectBuilderManager.getOrDefault(player.getUniqueId());
            if (slot >= builder.getSize()) {
                return;
            }
            logger.info("Slot is valid: " + slot);
            e.setCancelled(true);
            builder.handle(slot, player);
        };
        Bukkit.getPluginManager().registerEvents(this, managerDirector.getPlugin());
    }

    public ObjectDirector(ManagerDirector managerDirector,
                          String fileKey,
                          ObjectManager<T> objectManager) {
        super(managerDirector);
        this.objectBuilderManager = new ObjectBuilderManager<>(managerDirector,
                fileKey);
        this.objectManager = objectManager;
        clickEventConsumer = e -> {
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
        };
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        clickEventConsumer.accept(e);
    }

    public ObjectBuilderManager<T> getBuilderManager() {
        return objectBuilderManager;
    }

    public ObjectManager<T> getObjectManager() {
        return objectManager;
    }

    public void onInventoryClickEvent(Consumer<InventoryClickEvent> clickEventConsumer) {
        this.clickEventConsumer = clickEventConsumer;
    }
}
