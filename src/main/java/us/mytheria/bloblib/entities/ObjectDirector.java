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
import java.util.function.Consumer;
import java.util.function.Function;

public class ObjectDirector<T extends BlobObject> extends Manager implements Listener {
    private final ObjectBuilderManager<T> objectBuilderManager;
    private final ObjectManager<T> objectManager;
    private Consumer<InventoryClickEvent> clickEventConsumer;
    private final BlobExecutor executor;
    private final List<Function<ExecutorData, Boolean>> nonAdminChildCommands;
    private final List<Function<ExecutorData, Boolean>> adminChildCommands;
    private final List<Function<ExecutorData, List<String>>> nonAdminChildTabCompleter;
    private final List<Function<ExecutorData, List<String>>> adminChildTabCompleter;
    private final String objectName;
    private final boolean hasObjectBuilderManager;
    private boolean objectIsEditable;

    public ObjectDirector(ManagerDirector managerDirector,
                          ObjectDirectorData objectDirectorData,
                          Function<File, T> readFunction) {
        this(managerDirector, objectDirectorData, readFunction, true);
    }

    public ObjectDirector(ManagerDirector managerDirector,
                          ObjectDirectorData objectDirectorData,
                          Function<File, T> readFunction, boolean hasObjectBuilderManager) {
        super(managerDirector);
        objectIsEditable = false;
        this.hasObjectBuilderManager = hasObjectBuilderManager;
        if (hasObjectBuilderManager)
            this.objectBuilderManager = new ObjectBuilderManager<>(managerDirector,
                    objectDirectorData.objectBuilderKey(), this);
        else
            this.objectBuilderManager = null;
        Optional<File> loadFilesDirectory = managerDirector.getFileManager().searchFile(objectDirectorData.objectDirectory());
        if (loadFilesDirectory.isEmpty())
            throw new IllegalArgumentException("The loadFilesPathKey is not valid");
        this.objectManager = new ObjectManager<>(managerDirector, loadFilesDirectory.get(),
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
                        if (blobObject.edit() != null)
                            objectIsEditable = true;
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
        setDefaultCommands().setDefaultTabCompleter();
    }

    @Override
    public void reload() {
        getObjectManager().reload();
        if (getObjectBuilderManager() != null)
            getObjectBuilderManager().reload();
    }

    /**
     * This method is used to add non-admin child commands to the executor.
     * There have not been any checks done regarding permissions prior to this method.
     * <p>
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
     * @param nonAdminChildCommand the BlobChildCommands that CAN BE EXECUTED WITHOUT 'YOURPLUGIN.admin' permission.
     */
    public void addNonAdminChildCommand(Function<ExecutorData, Boolean> nonAdminChildCommand) {
        this.nonAdminChildCommands.add(nonAdminChildCommand);
    }

    /**
     * This method is used to add admin child commands to the executor.
     * The method BlobExecutor#hasAdminPermission was issued prior to this method.
     * <p>
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
     * @param adminChildCommand the BlobChildCommands that can ONLY be executed with 'YOURPLUGIN.admin' permission.
     */
    public void addAdminChildCommand(Function<ExecutorData, Boolean> adminChildCommand) {
        this.adminChildCommands.add(adminChildCommand);
    }

    /**
     * This method is used to add non-admin child tab completer to the executor.
     * There have not been any checks done regarding permissions prior to this method.
     * <p>
     * The BiFunction should take a BlobExecutor and a String[] as parameters and return a List&lt;String&gt;.
     * The BlobExecutor is the executor of the command, the String[] is the arguments of the command.
     * The List&lt;String&gt; are the list of tab completions. In case of not matching any tab completions,
     * return null. Otherwise, return the desired tab completions.
     *
     * @param nonAdminChildTabCompleter the BlobChildCommands that CAN BE EXECUTED WITHOUT 'YOURPLUGIN.admin' permission.
     */
    public void addNonAdminChildTabCompleter(Function<ExecutorData, List<String>> nonAdminChildTabCompleter) {
        this.nonAdminChildTabCompleter.add(nonAdminChildTabCompleter);
    }

    /**
     * This method is used to add admin child tab completer to the executor.
     * The method BlobExecutor#hasAdminPermission was issued prior to this method.
     * <p>
     * The BiFunction should take a BlobExecutor and a String[] as parameters and return a List&lt;String&gt;.
     * The BlobExecutor is the executor of the command, the String[] is the arguments of the command.
     * The List&lt;String&gt; are the list of tab completions. In case of not matching any tab completions,
     * return null. Otherwise, return the desired tab completions.
     *
     * @param adminChildTabCompleter the BlobChildCommands that can ONLY be executed with 'YOURPLUGIN.admin' permission.
     */
    public void addAdminChildTabCompleter(Function<ExecutorData, List<String>> adminChildTabCompleter) {
        this.adminChildTabCompleter.add(adminChildTabCompleter);
    }

    public List<Function<ExecutorData, Boolean>> getAdminChildCommands() {
        return adminChildCommands;
    }

    public List<Function<ExecutorData, Boolean>> getNonAdminChildCommands() {
        return nonAdminChildCommands;
    }

    public List<Function<ExecutorData, List<String>>> getAdminChildTabCompleter() {
        return adminChildTabCompleter;
    }

    public List<Function<ExecutorData, List<String>>> getNonAdminChildTabCompleter() {
        return nonAdminChildTabCompleter;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!this.hasObjectBuilderManager)
            return;
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
                    for (Function<ExecutorData, Boolean> childCommand : nonAdminChildCommands) {
                        if (childCommand.apply(new ExecutorData(executor, args, sender)))
                            return true;
                    }
                    if (!executor.hasAdminPermission(sender))
                        return true;
                    if (hasObjectBuilderManager) {
                        addAdminChildCommand(data -> {
                            Result<BlobChildCommand> result = data.executor()
                                    .isChildCommand("add", data.args());
                            if (result.isValid()) {
                                return getExecutor().ifInstanceOfPlayer(data.sender(), player -> {
                                    ObjectBuilder<T> builder = objectBuilderManager.getOrDefault(player.getUniqueId());
                                    builder.openInventory();
                                });
                            }
                            return false;
                        });
                        addAdminChildCommand(data -> {
                            Result<BlobChildCommand> result = data.executor()
                                    .isChildCommand("remove", data.args());
                            if (result.isValid()) {
                                return getExecutor().ifInstanceOfPlayer(data.sender(), this::removeObject);
                            }
                            return false;
                        });
                        addAdminChildCommand(data -> {
                            Result<BlobChildCommand> result = data.executor()
                                    .isChildCommand("edit", data.args());
                            if (result.isValid()) {
                                if (args.length != 2)
                                    return false;
                                String input = args[1];
                                return getExecutor().ifInstanceOfPlayer(data.sender(), player -> {
                                    editObject(player, input);
                                });
                            }
                            return false;
                        });
                    }
                    for (Function<ExecutorData, Boolean> childCommand : adminChildCommands) {
                        if (childCommand.apply(new ExecutorData(executor, args, sender)))
                            return true;
                    }
                    return false;
                }
        );
        return this;
    }

    private void setDefaultTabCompleter() {
        List<String> list = new ArrayList<>();
        getExecutor().setTabCompleter((sender, args) -> {
            for (Function<ExecutorData, List<String>> childTabCompleter : nonAdminChildTabCompleter) {
                List<String> childTabCompletion = childTabCompleter.apply(new ExecutorData(executor, args, sender));
                if (childTabCompletion != null)
                    return childTabCompletion;
            }
            if (!executor.hasAdminPermission(sender))
                return list;
            if (hasObjectBuilderManager)
                addAdminChildTabCompleter(data -> {
                    List<String> stringList = new ArrayList<>();
                    stringList.add("add");
                    stringList.add("remove");
                    if (objectIsEditable)
                        stringList.add("edit");
                    return stringList;
                });
            for (Function<ExecutorData, List<String>> childTabCompleter : adminChildTabCompleter) {
                List<String> childTabCompletion = childTabCompleter.apply(new ExecutorData(executor, args, sender));
                if (childTabCompletion != null && !childTabCompletion.isEmpty())
                    return childTabCompletion;
            }
            return list;
        });
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
            Object object = getObjectManager().getObject(key);
            if (ItemStack.class.isInstance(object.getClass())) {
                ItemStack itemStack = (ItemStack) object;
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
            BlobLibAssetAPI.getMessage("Editor.Removed")
                    .modify(s -> s.replace("%element%", key))
                    .sendAndPlay(player);
        }, function);
    }

    public boolean objectIsEditable() {
        return objectIsEditable;
    }

    @SuppressWarnings("unchecked")
    public void editObject(Player player, String key) {
        if (!objectIsEditable) {
            BlobLibAssetAPI.getMessage("Object.Not-Editable").sendAndPlay(player);
            return;
        }
        T object = objectManager.getObject(key);
        if (object == null) {
            BlobLibAssetAPI.getMessage("Object.Not-Found").sendAndPlay(player);
            return;
        }
        ObjectBuilder<T> builder = (ObjectBuilder<T>) object.edit();
        getObjectBuilderManager().addBuilder(player.getUniqueId(), builder);
    }
}
