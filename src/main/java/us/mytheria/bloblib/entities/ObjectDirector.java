package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.Result;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.inventory.ObjectBuilder;
import us.mytheria.bloblib.itemstack.ItemStackBuilder;
import us.mytheria.bloblib.managers.Manager;
import us.mytheria.bloblib.managers.ManagerDirector;
import us.mytheria.bloblib.utilities.ItemStackUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ObjectDirector<T extends BlobObject> extends Manager implements Listener {
    private final ObjectBuilderManager<T> objectBuilderManager;
    private final ObjectManager<T> objectManager;
    private Consumer<InventoryClickEvent> clickEventConsumer;
    private final BlobExecutor executor;
    private List<BiFunction<BlobExecutor, String[], Boolean>> nonAdminChildCommands;
    private List<BiFunction<BlobExecutor, String[], Boolean>> adminChildCommands;
    private List<BiFunction<BlobExecutor, String[], List<String>>> nonAdminChildTabCompleter;
    private List<BiFunction<BlobExecutor, String[], List<String>>> adminChildTabCompleter;
    private final String objectName;

    public ObjectDirector(ManagerDirector managerDirector,
                          ObjectDirectorData objectDirectorData,
                          Function<File, T> readFunction) {
        super(managerDirector);
        this.objectBuilderManager = new ObjectBuilderManager<>(managerDirector,
                objectDirectorData.objectBuilderKey(), this);
        Optional<File> loadFilesPath = managerDirector.getFileManager().searchFile(objectDirectorData.objectDirectory());
        if (loadFilesPath.isEmpty())
            throw new IllegalArgumentException("The loadFilesPathKey is not valid");
        this.objectManager = new ObjectManager<>(managerDirector, loadFilesPath.get(),
                HashMap::new, HashMap::new) {
            @Override
            public void loadFiles(File path) {
                if (!path.exists())
                    path.mkdir();
                File[] listOfFiles = path.listFiles();
                for (File file : listOfFiles) {
                    if (file.getName().equals(".DS_Store"))
                        continue;
                    if (file.isFile()) {
                        T blobObject = readFunction.apply(file);
                        addObject(blobObject.getKey(), blobObject, file);
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
        nonAdminChildTabCompleter = new ArrayList<>();
        adminChildTabCompleter = new ArrayList<>();
        executor = new BlobExecutor(getPlugin(), objectDirectorData.objectName());
        objectName = objectDirectorData.objectName();
        setDefaultCommands();
        setDefaultTabCompleter();
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
        this.objectName = objectName;
        nonAdminChildCommands = new ArrayList<>();
        adminChildCommands = new ArrayList<>();
        nonAdminChildTabCompleter = new ArrayList<>();
        adminChildTabCompleter = new ArrayList<>();
        setDefaultCommands();
        setDefaultTabCompleter();
    }

    @Override
    public void reload() {
        getObjectManager().reload();
        getObjectBuilderManager().reload();
    }

    /**
     * This method is used to add non-admin child commands to the executor.
     * There have not been any checks done regarding permissions prior to this method.
     * <p>
     * You need to initialize a List (such as an ArrayList) and add BiFunctions to it.
     * The BiFunction should take a BlobExecutor and a String[] as parameters and return a Boolean.
     * The BlobExecutor is the executor of the command, the String[] is the arguments of the command.
     * The Boolean should ONLY BE true if the command was executed successfully and no more code
     * needs to be executed related to the parent command. Else, it should be false since
     * it will continue to execute the other child commands.
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
     * @param nonAdminChildCommands the BlobChildCommands that CAN BE EXECUTED WITHOUT 'YOURPLUGIN.admin' permission.
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
     * The Boolean should ONLY BE true if the command was executed successfully and no more code
     * needs to be executed related to the parent command. Else, it should be false since
     * it will continue to execute the other child commands.
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

    /**
     * This method is used to add non-admin child tab completer to the executor.
     * There have not been any checks done regarding permissions prior to this method.
     * <p>
     * You need to initialize a List (such as an ArrayList) and add BiFunctions to it.
     * The BiFunction should take a BlobExecutor and a String[] as parameters and return a List&lt;String&gt;.
     * The BlobExecutor is the executor of the command, the String[] is the arguments of the command.
     * The List&lt;String&gt; are the list of tab completions. In case of not matching any tab completions,
     * return null. Otherwise, return the desired tab completions.
     *
     * @param nonAdminChildTabCompleter the BlobChildCommands that CAN BE EXECUTED WITHOUT 'YOURPLUGIN.admin' permission.
     */
    public void setNonAdminChildTabCompleter(List<BiFunction<BlobExecutor, String[], List<String>>> nonAdminChildTabCompleter) {
        this.nonAdminChildTabCompleter = nonAdminChildTabCompleter;
    }

    /**
     * This method is used to add admin child tab completer to the executor.
     * The method BlobExecutor#hasAdminPermission was issued prior to this method.
     * <p>
     * You need to initialize a List (such as an ArrayList) and add BiFunctions to it.
     * The BiFunction should take a BlobExecutor and a String[] as parameters and return a List&lt;String&gt;.
     * The BlobExecutor is the executor of the command, the String[] is the arguments of the command.
     * The List&lt;String&gt; are the list of tab completions. In case of not matching any tab completions,
     * return null. Otherwise, return the desired tab completions.
     *
     * @param adminChildTabCompleter the BlobChildCommands that can ONLY be executed with 'YOURPLUGIN.admin' permission.
     */
    public void setAdminChildTabCompleter(List<BiFunction<BlobExecutor, String[], List<String>>> adminChildTabCompleter) {
        this.adminChildTabCompleter = adminChildTabCompleter;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        clickEventConsumer.accept(e);
    }

    public ObjectBuilderManager<T> getBuilderManager() {
        return objectBuilderManager;
    }

    private ObjectBuilderManager<T> getObjectBuilderManager() {
        return objectBuilderManager;
    }

    public ObjectManager<T> getObjectManager() {
        return objectManager;
    }

    public void onInventoryClickEvent(Consumer<InventoryClickEvent> clickEventConsumer) {
        this.clickEventConsumer = clickEventConsumer;
    }

    private BlobExecutor getExecutor() {
        return executor;
    }

    private ObjectDirector<T> setDefaultCommands() {
        getExecutor().setCommand((sender, args) -> {
            if (executor.hasNoArguments(sender, args))
                return true;
            for (BiFunction<BlobExecutor, String[], Boolean> childCommand : nonAdminChildCommands) {
                if (childCommand.apply(executor, args))
                    return true;
            }
            if (!executor.hasAdminPermission(sender))
                return true;
            for (BiFunction<BlobExecutor, String[], Boolean> childCommand : adminChildCommands) {
                if (childCommand.apply(executor, args))
                    return true;
            }
            Result<BlobChildCommand> addResult = getExecutor()
                    .isChildCommand("add", args);
            if (addResult.isValid()) {
                return getExecutor().ifInstanceOfPlayer(sender, player -> {
                    ObjectBuilder<T> builder = objectBuilderManager.getOrDefault(player.getUniqueId());
                    builder.openInventory();
                });
            }
            Result<BlobChildCommand> removeResult = getExecutor()
                    .isChildCommand("remove", args);
            if (removeResult.isValid()) {
                return getExecutor().ifInstanceOfPlayer(sender, this::removeObject);
            }
            return false;
        });
        return this;
    }

    private ObjectDirector<T> setDefaultTabCompleter() {
        List<String> list = new ArrayList<>();
        getExecutor().setTabCompleter((sender, args) -> {
            for (BiFunction<BlobExecutor, String[], List<String>> childTabCompleter : nonAdminChildTabCompleter) {
                List<String> childTabCompletion = childTabCompleter.apply(executor, args);
                if (childTabCompletion != null)
                    return childTabCompletion;
            }
            if (!executor.hasAdminPermission(sender))
                return list;
            for (BiFunction<BlobExecutor, String[], List<String>> childTabCompleter : adminChildTabCompleter) {
                List<String> childTabCompletion = childTabCompleter.apply(executor, args);
                if (childTabCompletion != null && !childTabCompletion.isEmpty())
                    return childTabCompletion;
            }
            list.add("add");
            list.add("remove");
            return list;
        });
        return this;
    }

    /**
     * This method is used to add an object to the object manager.
     * <p>
     * Has default support in case the ObjectManager is T extends ItemStack.
     * Being this the case, will display null if the ItemStack is null.
     * Will display the Material if the ItemStack has no ItemMeta or
     * has no display name. Will display the display name if the ItemStack
     * has ItemMeta and has a display name.
     * <p>
     * In case of not being an ItemStack, will use the object's key
     * as the display name and an empty lore.
     *
     * @param player the player who is removing the object.
     */
    public void removeObject(Player player) {
        removeObject(player, key -> {
            ItemStackBuilder builder = ItemStackBuilder.build(Material.COMMAND_BLOCK);
            builder.displayName(key);
            builder.lore();
            T object = getObjectManager().getObject(key);
            if (object instanceof ItemStack itemStack) {
                builder.displayName(ItemStackUtil.display(itemStack));
                builder.lore();
            }
            return builder.build();
        });
    }

    /**
     * This method is used to remove an object from the object manager.
     *
     * @param player   the player who is removing the object.
     * @param function the function that is used to get the ItemStack to display
     *                 the object to be removed.
     */
    public void removeObject(Player player, Function<String, ItemStack> function) {
        BlobEditor<String> editor = objectManager.makeEditor(player, objectName);
        editor.removeElement(player, key -> {
            player.closeInventory();
            getObjectManager().removeObject(key);
            BlobLibAssetAPI.getMessage("Editor.Removed").sendAndPlay(player);
        }, function);
    }
}
