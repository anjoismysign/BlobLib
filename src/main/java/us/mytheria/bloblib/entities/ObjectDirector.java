package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.Result;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.api.BlobLibMessageAPI;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;
import us.mytheria.bloblib.entities.inventory.ObjectBuilder;
import us.mytheria.bloblib.itemstack.ItemStackBuilder;
import us.mytheria.bloblib.managers.Manager;
import us.mytheria.bloblib.managers.ManagerDirector;
import us.mytheria.bloblib.utilities.ItemStackUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class ObjectDirector<T extends BlobObject> extends Manager
        implements Listener, RunnableReloadable {
    private final ObjectBuilderManager<T> objectBuilderManager;
    private final ObjectManager<T> objectManager;
    private final CommandDirector commandDirector;
    protected final String objectName;
    private final boolean hasObjectBuilderManager;
    private boolean objectIsEditable;

    public ObjectDirector(ManagerDirector managerDirector,
                          ObjectDirectorData objectDirectorData,
                          Function<File, T> readFunction) {
        this(managerDirector, objectDirectorData, readFunction, true, true);
    }

    public ObjectDirector(ManagerDirector managerDirector,
                          ObjectDirectorData objectDirectorData,
                          Function<File, T> readFunction,
                          boolean hasObjectBuilderManager) {
        this(managerDirector, objectDirectorData, readFunction, hasObjectBuilderManager, true);
    }

    public ObjectDirector(ManagerDirector managerDirector,
                          ObjectDirectorData objectDirectorData,
                          Function<File, T> readFunction,
                          boolean hasObjectBuilderManager,
                          boolean isAsynchronous) {
        super(managerDirector);
        this.commandDirector = new CommandDirector(managerDirector.getPlugin(), objectDirectorData.objectName());
        objectIsEditable = false;
        this.hasObjectBuilderManager = hasObjectBuilderManager;
        if (hasObjectBuilderManager) {
            this.objectBuilderManager = new ObjectBuilderManager<>(managerDirector,
                    objectDirectorData.objectBuilderKey(), this);
        } else {
            this.objectBuilderManager = null;
        }
        Optional<File> loadFilesDirectory = managerDirector.getRealFileManager().searchFile(objectDirectorData.objectDirectory());
        if (loadFilesDirectory.isEmpty()) {
            Bukkit.getLogger().info("The loadFilesPathKey is not valid");
            throw new IllegalArgumentException("The loadFilesPathKey is not valid");
        }
        this.objectManager = isAsynchronous ? new AsynchronousObjectManager<>(managerDirector, loadFilesDirectory.get(),
                ConcurrentHashMap::new, ConcurrentHashMap::new, this, readFunction) :
                new SynchronousObjectManager<>(managerDirector, loadFilesDirectory.get(),
                        ConcurrentHashMap::new, ConcurrentHashMap::new, this, readFunction);
        Bukkit.getPluginManager().registerEvents(this, managerDirector.getPlugin());
        objectName = objectDirectorData.objectName();
        setDefaultCommands().setDefaultTabCompleter();
        if (hasObjectBuilderManager) {
            addAdminChildTabCompleter(executorData -> {
                String[] args = executorData.args();
                if (args.length != 1)
                    return null;
                List<String> list = new ArrayList<>();
                list.add("add");
                list.add("remove");
                if (objectIsEditable)
                    list.add("edit");
                return list;
            });
            addAdminChildCommand(data -> {
                Result<BlobChildCommand> result = data.executor()
                        .isChildCommand("add", data.args());
                if (result.isValid()) {
                    return commandDirector.getExecutor().ifInstanceOfPlayer(data.sender(), player -> {
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
                    return commandDirector.getExecutor().ifInstanceOfPlayer(data.sender(), this::removeObject);
                }
                return false;
            });
            addAdminChildCommand(data -> {
                String[] args = data.args();
                Result<BlobChildCommand> result = data.executor()
                        .isChildCommand("edit", data.args());
                if (result.isValid()) {
                    if (args.length != 2)
                        return false;
                    String input = args[1];
                    return commandDirector.getExecutor().ifInstanceOfPlayer(data.sender(), player -> editObject(player, input));
                }
                return false;
            });
        }
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
        this.commandDirector.addNonAdminChildCommand(nonAdminChildCommand);
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
        this.commandDirector.addAdminChildCommand(adminChildCommand);
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
        this.commandDirector.addNonAdminChildTabCompleter(nonAdminChildTabCompleter);
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
        this.commandDirector.addAdminChildTabCompleter(adminChildTabCompleter);
    }

    public List<Function<ExecutorData, Boolean>> getAdminChildCommands() {
        return this.commandDirector.getAdminChildCommands();
    }

    public List<Function<ExecutorData, Boolean>> getNonAdminChildCommands() {
        return this.commandDirector.getNonAdminChildCommands();
    }

    public List<Function<ExecutorData, List<String>>> getAdminChildTabCompleter() {
        return this.commandDirector.getAdminChildTabCompleter();
    }

    public List<Function<ExecutorData, List<String>>> getNonAdminChildTabCompleter() {
        return this.commandDirector.getNonAdminChildTabCompleter();
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

    private ObjectDirector<T> setDefaultCommands() {
        commandDirector.setDefaultCommands();
        return this;
    }

    private void setDefaultTabCompleter() {
        commandDirector.setDefaultTabCompleter();
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
        removeObject(player, object -> {
            String key = object.getKey();
            ItemStackBuilder builder = ItemStackBuilder.build(Material.COMMAND_BLOCK);
            builder.displayName(key);
            builder.lore(BlobLibTranslatableAPI.getInstance()
                    .getTranslatableBlock("BlobLib.Remove-Element", player).get());
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
    public void removeObject(Player player, Function<T, ItemStack> function) {
        BlobEditor<T> editor = objectManager.makeEditor(player);
        editor.removeElement(player, element -> {
            String key = element.getKey();
            player.closeInventory();
            getObjectManager().removeObject(key);
            BlobLibMessageAPI.getInstance().getMessage("Editor.Removed")
                    .modify(s -> s.replace("%element%", key))
                    .handle(player);
        }, function);
    }

    public boolean objectIsEditable() {
        return objectIsEditable;
    }

    public void setObjectIsEditable(boolean objectIsEditable) {
        this.objectIsEditable = objectIsEditable;
    }

    @SuppressWarnings("unchecked")
    public void editObject(Player player, String key) {
        if (!objectIsEditable) {
            BlobLibMessageAPI.getInstance().getMessage("Object.Not-Editable").handle(player);
            return;
        }
        T object = objectManager.getObject(key);
        if (object == null) {
            BlobLibMessageAPI.getInstance().getMessage("Object.Not-Found").handle(player);
            return;
        }
        ObjectBuilder<T> builder = (ObjectBuilder<T>) object.edit();
        getObjectBuilderManager().addBuilder(player.getUniqueId(), builder);
    }

    public void whenObjectManagerFilesLoad(Consumer<ObjectManager<T>> consumer) {
        this.objectManager.whenFilesLoad(consumer);
    }

    public boolean hasObjectBuilderManager() {
        return hasObjectBuilderManager;
    }

    @NotNull
    public ObjectBuilder<T> getOrDefaultBuilder(UUID uuid) {
        if (!hasObjectBuilderManager)
            throw new IllegalStateException("ObjectBuilderManager is not enabled. " +
                    "Implement it in constructor.");
        return getObjectBuilderManager().getOrDefault(uuid);
    }

    @Override
    public boolean isReloading() {
        return getObjectManager().isReloading();
    }

    @Override
    public void whenReloaded(Runnable runnable) {
        getObjectManager().whenReloaded(runnable);
    }
}
