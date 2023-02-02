package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.Result;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ObjectDirector<T> extends Manager implements Listener {
    private final ObjectBuilderManager<T> objectBuilderManager;
    private final ObjectManager<T> objectManager;
    private Consumer<InventoryClickEvent> clickEventConsumer;
    private final BlobExecutor executor;
    private List<BiFunction<BlobExecutor, String[], Boolean>> nonAdminChildCommands;
    private List<BiFunction<BlobExecutor, String[], Boolean>> adminChildCommands;

    public ObjectDirector(ManagerDirector managerDirector,
                          ObjectDirectorData objectDirectorData,
                          Function<File, Tuple2<T, String>> readFunction) {
        super(managerDirector);
        this.objectBuilderManager = new ObjectBuilderManager<>(managerDirector,
                objectDirectorData.objectBuilderKey(), this);
        Optional<File> loadFilesPath = managerDirector.getFileManager().searchFile(objectDirectorData.objectDirectory());
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
                        addObject(tuple.second(), tuple.first());
                    }
                    if (file.isDirectory())
                        loadFiles(file);
                }
            }
        };
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
        Bukkit.getPluginManager().registerEvents(this, managerDirector.getPlugin());
        nonAdminChildCommands = new ArrayList<>();
        adminChildCommands = new ArrayList<>();
        executor = new BlobExecutor(getPlugin(), objectDirectorData.objectName());
    }

    public ObjectDirector(ManagerDirector managerDirector,
                          String fileKey,
                          ObjectManager<T> objectManager,
                          String objectName) {
        super(managerDirector);
        this.objectBuilderManager = new ObjectBuilderManager<>(managerDirector,
                fileKey, this);
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
        executor = new BlobExecutor(getPlugin(), objectName);
        nonAdminChildCommands = new ArrayList<>();
        adminChildCommands = new ArrayList<>();
        getExecutor().setCommand((sender, args) -> {
            if (executor.hasNoArguments(sender, args))
                return true;
            nonAdminChildCommands.forEach(childCommand -> childCommand.apply(executor, args));
            if (!executor.hasAdminPermission(sender))
                return true;
            adminChildCommands.forEach(childCommand -> childCommand.apply(executor, args));
            Result<BlobChildCommand> addResult = getExecutor()
                    .isChildCommand("add", args);
            if (addResult.isValid()) {
                return getExecutor().ifInstanceOfPlayer(sender, player -> {
                    ObjectBuilder<T> builder = objectBuilderManager.getOrDefault(player.getUniqueId());
                    builder.openInventory();
                });
            }
            return false;
        });
    }

    /**
     * This method is used to add non-admin child commands to the executor.
     * There have not been any checks done regarding permissions prior to this method.
     * <p>
     * You need to initialize a List (such as an ArrayList) and add BiFunctions to it.
     * The BiFunction should take a BlobExecutor and a String[] as parameters and return a Boolean.
     * The BlobExecutor is the executor of the command, the String[] is the arguments of the command.
     * The Boolean should ONLY BE true if the command was executed successfully, false otherwise.
     * <p>
     * An example would be:
     * <pre>
     *     List&lt;BiFunction&lt;BlobExecutor, String[], Boolean&gt;&gt; childCommands =
     *     new ArrayList&lt;&gt;();
     *     childCommands.add((executor, args) -&gt; {
     *      Result&lt;BlobChildCommand&gt; childCommand = executor.isChildCommand
     *      ("alwaysday", args);
     *      if (childCommand.isValid()) {
     *          return executor.ifInstanceOfPlayer(sender, player -&gt; {
     *              player.setPlayerTime(6000, false);
     *          });
     *      }
     *     return false;
     *     });
     *     setNonAdminChildCommands(childCommands);
     *     </pre>
     *
     * @param nonAdminChildCommands the BlobChildCommands that can be executed without 'YOURPLUGIN.admin' permission.
     */
    public void setNonAdminChildCommands(List<BiFunction<BlobExecutor, String[], Boolean>> nonAdminChildCommands) {
        this.nonAdminChildCommands = nonAdminChildCommands;
    }

    /**
     * This method is used to add admin child commands to the executor.
     * The method BlobExecutor#hasAdminPermission was issued prior to this method.
     * <p>
     * You need to initialize a List (such as an ArrayList) and add BiFunctions to it.
     * The BiFunction should take a BlobExecutor and a String[] as parameters and return a Boolean.
     * The BlobExecutor is the executor of the command, the String[] is the arguments of the command.
     * The Boolean should ONLY BE true if the command was executed successfully, false otherwise.
     * <p>
     * * An example would be:
     * * <pre>
     *      *     List&lt;BiFunction&lt;BlobExecutor, String[], Boolean&gt;&gt; childCommands =
     *      *     new ArrayList&lt;&gt;();
     *      *     childCommands.add((executor, args) -&gt; {
     *      *      Result&lt;BlobChildCommand&gt; childCommand = executor.isChildCommand
     *      *      ("demoscreen", args);
     *      *      if (childCommand.isValid()) {
     *      *          return executor.ifInstanceOfPlayer(sender, player -&gt; {
     *      *              player.showDemoScreen();
     *      *          });
     *      *      }
     *      *     return false;
     *      *     });
     *      *     setAdminChildCommands(childCommands);
     *      *     </pre>
     *
     * @param adminChildCommands the BlobChildCommands that can ONLY be executed with 'YOURPLUGIN.admin' permission.
     */
    public void setAdminChildCommands(List<BiFunction<BlobExecutor, String[], Boolean>> adminChildCommands) {
        this.adminChildCommands = adminChildCommands;
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

    public BlobExecutor getExecutor() {
        return executor;
    }
}
