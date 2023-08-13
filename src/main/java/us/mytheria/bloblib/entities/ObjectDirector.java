package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.Result;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.inventory.ObjectBuilder;
import us.mytheria.bloblib.itemstack.ItemStackBuilder;
import us.mytheria.bloblib.managers.Manager;
import us.mytheria.bloblib.managers.ManagerDirector;
import us.mytheria.bloblib.utilities.ItemStackUtil;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

public class ObjectDirector<T extends BlobObject> extends Manager implements Listener {
    private final ObjectBuilderManager<T> objectBuilderManager;
    private final ObjectManager<T> objectManager;
    private Consumer<InventoryClickEvent> clickEventConsumer;
    private final BlobExecutor executor;
    private final List<Function<ExecutorData, Boolean>> nonAdminChildCommands;
    private final List<Function<ExecutorData, Boolean>> adminChildCommands;
    private final List<Function<ExecutorData, List<String>>> nonAdminChildTabCompleter;
    private final List<Function<ExecutorData, List<String>>> adminChildTabCompleter;
    protected final String objectName;
    private final boolean hasObjectBuilderManager;
    private boolean objectIsEditable;
    protected CompletableFuture<Void> loadFilesFuture;
    private final Consumer<Player> addConsumer;

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
        if (hasObjectBuilderManager) {
            this.objectBuilderManager = new ObjectBuilderManager<>(managerDirector,
                    objectDirectorData.objectBuilderKey(), this);
            this.addConsumer = objectBuilderManager::getOrDefault;
        } else {
            this.objectBuilderManager = null;
            this.addConsumer = player -> {
            };
        }
        Optional<File> loadFilesDirectory = managerDirector.getRealFileManager().searchFile(objectDirectorData.objectDirectory());
        if (loadFilesDirectory.isEmpty()) {
            Bukkit.getLogger().info("The loadFilesPathKey is not valid");
            throw new IllegalArgumentException("The loadFilesPathKey is not valid");
        }
        this.objectManager = new ObjectManager<>(managerDirector, loadFilesDirectory.get(),
                ConcurrentHashMap::new, ConcurrentHashMap::new, this) {
            public void loadFiles(File path, CompletableFuture<Void> mainFuture) {
                try {
                    if (!path.exists())
                        path.mkdir();
                    Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
                        try {
                            String[] extensions = {"yml"};
                            Collection<File> files = FileUtils.listFiles(path, extensions, true);
                            List<CompletableFuture<Void>> futures = new ArrayList<>();
                            files.forEach(file -> {
                                CompletableFuture<Void> fileFuture = CompletableFuture.runAsync(() -> {
                                    try {
                                        T blobObject = readFunction.apply(file);
                                        if (blobObject != null) {
                                            if (blobObject.edit() != null)
                                                objectIsEditable = true;
                                            this.addObject(blobObject.getKey(), blobObject, file);
                                        }
                                    } catch (Exception e) {
                                        Bukkit.getLogger().log(Level.SEVERE, e.getMessage() + "\n" +
                                                "At: " + file.getPath(), e);
                                    }
                                });
                                futures.add(fileFuture);
                            });
                            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenAccept(v -> mainFuture.complete(null));
                        } catch (Exception e) {
                            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
                            mainFuture.completeExceptionally(e);
                        }
                    });
                } catch (Exception exception) {
                    Bukkit.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
                    mainFuture.completeExceptionally(exception);
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
                String[] args = data.args();
                Result<BlobChildCommand> result = data.executor()
                        .isChildCommand("edit", data.args());
                if (result.isValid()) {
                    if (args.length != 2)
                        return false;
                    String input = args[1];
                    return getExecutor().ifInstanceOfPlayer(data.sender(), player -> editObject(player, input));
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
        getExecutor().setTabCompleter((sender, args) -> {
            List<String> suggestions = new ArrayList<>();
            for (Function<ExecutorData, List<String>> childTabCompleter : nonAdminChildTabCompleter) {
                List<String> childTabCompletion = childTabCompleter.apply(new ExecutorData(executor, args, sender));
                if (childTabCompletion != null && !childTabCompletion.isEmpty())
                    suggestions.addAll(childTabCompletion);
            }
            if (!executor.hasAdminPermission(sender, null))
                return suggestions;
            for (Function<ExecutorData, List<String>> childTabCompleter : adminChildTabCompleter) {
                List<String> childTabCompletion = childTabCompleter.apply(new ExecutorData(executor, args, sender));
                if (childTabCompletion != null && !childTabCompletion.isEmpty())
                    suggestions.addAll(childTabCompletion);
            }
            return suggestions;
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
        removeObject(player, object -> {
            String key = object.getKey();
            ItemStackBuilder builder = ItemStackBuilder.build(Material.COMMAND_BLOCK);
            builder.displayName(key);
            builder.lore();
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
            BlobLibAssetAPI.getMessage("Editor.Removed")
                    .modify(s -> s.replace("%element%", key))
                    .handle(player);
        }, function);
    }

    public boolean objectIsEditable() {
        return objectIsEditable;
    }

    @SuppressWarnings("unchecked")
    public void editObject(Player player, String key) {
        if (!objectIsEditable) {
            BlobLibAssetAPI.getMessage("Object.Not-Editable").handle(player);
            return;
        }
        T object = objectManager.getObject(key);
        if (object == null) {
            BlobLibAssetAPI.getMessage("Object.Not-Found").handle(player);
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
}
