package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.Uber;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.inventory.BlobInventory;
import us.mytheria.bloblib.entities.inventory.ObjectBuilder;
import us.mytheria.bloblib.entities.inventory.VariableSelector;
import us.mytheria.bloblib.entities.listeners.BlobEditorListener;
import us.mytheria.bloblib.entities.listeners.BlobSelectorListener;
import us.mytheria.bloblib.managers.SelectorListenerManager;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author anjoismysign
 * A BlobEditor is a VariableEditor that can be used to edit a collection.
 */
public class BlobEditor<T> extends VariableSelector<T> implements VariableEditor<T> {
    private final List<T> list;
    private final Collection<T> collection;
    private final SelectorListenerManager selectorManager;
    private final Consumer<Player> addConsumer;
    private Consumer<T> removeConsumer;
    private Function<T, ItemStack> buildFunction;

    /**
     * Creates a new BlobEditor passing a BlobInventory for VariableSelector
     * and injects a collection.
     *
     * @param <T>           the type of the collection
     * @param blobInventory the BlobInventory
     * @param builderId     the id of the builder
     * @param dataType      the data type of the editor
     * @param addConsumer   the consumer to add a new element
     * @param collection    the collection to inject
     * @return the new BlobEditor
     */
    public static <T> BlobEditor<T> build(BlobInventory blobInventory, UUID builderId,
                                          String dataType, Consumer<Player> addConsumer,
                                          Collection<T> collection,
                                          @Nullable Consumer<Player> onReturn) {
        return new BlobEditor<>(blobInventory, builderId,
                dataType, collection, addConsumer, onReturn);
    }

    /**
     * Creates a new BlobEditor passing a BlobInventory for VariableSelector
     *
     * @param <T>           the type of the collection
     * @param blobInventory the BlobInventory
     * @param builderId     the id of the builder
     * @param dataType      the data type of the editor
     * @param addConsumer   the consumer to add a new element
     * @return the new BlobEditor
     */
    public static <T> BlobEditor<T> build(BlobInventory blobInventory, UUID builderId,
                                          String dataType, Consumer<Player> addConsumer,
                                          @Nullable Consumer<Player> onReturn) {
        return new BlobEditor<>(blobInventory, builderId,
                dataType, addConsumer, onReturn);
    }

    /**
     * Creates a new BlobEditor
     *
     * @param <T>         the type of the collection
     * @param builderId   the id of the builder
     * @param dataType    the data type of the editor
     * @param addConsumer the consumer to add a new element
     * @return the new BlobEditor
     */
    public static <T> BlobEditor<T> DEFAULT(UUID builderId, String dataType,
                                            Consumer<Player> addConsumer,
                                            @Nullable Consumer<Player> onReturn) {
        Player get = Objects.requireNonNull(Bukkit.getPlayer(builderId));
        return new BlobEditor<>(VariableSelector.DEFAULT(get), builderId,
                dataType, addConsumer, onReturn);
    }

    /**
     * Creates a new BlobEditor passing specific collection.
     *
     * @param <T>         the type of the collection
     * @param builderId   the id of the builder
     * @param dataType    the data type of the editor
     * @param collection  the collection to edit
     * @param addConsumer the consumer to add new elements
     * @return the new BlobEditor
     */
    public static <T> BlobEditor<T> COLLECTION_INJECTION(UUID builderId, String dataType,
                                                         Collection<T> collection,
                                                         Consumer<Player> addConsumer,
                                                         @Nullable Consumer<Player> onReturn) {
        return new BlobEditor<>(VariableSelector.DEFAULT(), builderId,
                dataType, collection, addConsumer, onReturn);
    }

    /**
     * Creates a new BlobEditor passing an ObjectDirector as a new elements' provider.
     *
     * @param builderId the id of the builder
     * @param director  the ObjectDirector
     * @param <T>       the type of the collection
     * @return the new BlobEditor
     */
    public static <T extends BlobObject> BlobEditor<T> DEFAULT_DIRECTOR(UUID builderId,
                                                                        ObjectDirector<T> director,
                                                                        @Nullable Consumer<Player> onReturn) {
        Uber<BlobEditor<T>> uber = Uber.fly();
        uber.talk(BlobEditor.DEFAULT(builderId, director.objectName, player -> {
            ObjectManager<T> dropObjectManager = director.getObjectManager();
            BlobEditor<T> editor = dropObjectManager.makeEditor(player);
            editor.selectElement(player, element -> uber.thanks().add(element));
        }, onReturn));
        return uber.thanks();
    }

    /**
     * Creates a new BlobEditor passing an ObjectDirector's ObjectBuilder as a new elements' provider.
     *
     * @param builderId  the id of the builder
     * @param collection the collection to edit
     * @param director   the ObjectDirector
     * @param <T>        the type of the collection
     * @return the new BlobEditor
     */
    public static <T extends BlobObject> BlobEditor<T> COLLECTION_INJECTION_BUILDER(UUID builderId,
                                                                                    Collection<T> collection,
                                                                                    ObjectDirector<T> director,
                                                                                    @Nullable Consumer<Player> onReturn) {
        Uber<BlobEditor<T>> uber = Uber.fly();
        if (!director.hasObjectBuilderManager())
            throw new IllegalArgumentException("The director does not have an ObjectBuilderManager. " +
                    "Implement it in constructor.");
        Player get = Objects.requireNonNull(Bukkit.getPlayer(builderId));
        uber.talk(new BlobEditor<>(VariableSelector.DEFAULT(get), builderId,
                director.objectName, collection, player -> {
            ObjectBuilder<T> builder = director.getOrDefaultBuilder(player.getUniqueId());
            builder.open(player);
        }, onReturn));
        return uber.thanks();
    }

    protected BlobEditor(BlobInventory blobInventory, UUID builderId,
                         String dataType,
                         Consumer<Player> addConsumer,
                         @Nullable Consumer<Player> consumer) {
        super(blobInventory, builderId, dataType, null, consumer);
        this.selectorManager = BlobLib.getInstance().getSelectorManager();
        this.list = new ArrayList<>();
        this.collection = null;
        this.addConsumer = addConsumer;
        this.buildFunction = null;
    }

    protected BlobEditor(BlobInventory blobInventory, UUID builderId,
                         String dataType, Collection<T> collection,
                         Consumer<Player> addConsumer,
                         @Nullable Consumer<Player> consumer) {
        super(Objects.requireNonNull(blobInventory, "'blobInventory' cannot be null"),
                Objects.requireNonNull(builderId, "'builderId' cannot be null"),
                Objects.requireNonNull(dataType, "'dataType' cannot be null"),
                null, consumer);
        this.selectorManager = BlobLib.getInstance().getSelectorManager();
        this.collection = collection;
        this.list = null;
        this.addConsumer = addConsumer;
        this.buildFunction = null;
    }

    /**
     * Gets the remove consumer
     *
     * @return the remove consumer
     */
    @Nullable
    public Consumer<T> getRemoveConsumer() {
        return removeConsumer;
    }

    /**
     * any actions that should be executed inside the constructor on super call
     */
    @Override
    public void loadFirstPage() {
    }

    /**
     * loads the page with the given page number
     *
     * @param page                  the page number
     * @param whiteBackgroundRefill if the background should be refilled
     */
    @Override
    public void loadPage(int page, boolean whiteBackgroundRefill) {
        if (page < 1) {
            return;
        }
        if (getTotalPages() < page) {
            return;
        }
        if (whiteBackgroundRefill)
            refillButton("White-Background");
        clearValues();
        List<VariableValue<T>> values = this.page(page, getItemsPerPage());
        for (int i = 0; i < values.size(); i++) {
            setValue(i, values.get(i));
        }
    }

    /**
     * returns the page with the given page number without loading
     *
     * @param page         the page number
     * @param itemsPerPage the items per page
     */
    @Override
    public List<VariableValue<T>> page(int page, int itemsPerPage) {
        int start = (page - 1) * itemsPerPage;
        int end = start + (itemsPerPage);
        ArrayList<VariableValue<T>> values = new ArrayList<>();
        for (int i = start; i < end; i++) {
            T get;
            try {
                get = getList().get(i);
                ItemStack itemStack = new ItemStack(Material.LEATHER_CHESTPLATE);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ChatColor.GOLD + get.toString());
                itemStack.setItemMeta(itemMeta);
                values.add(new VariableValue<>(itemStack, get));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        return values;
    }

    /**
     * Selects an object from the editor.
     *
     * @param player          The player that selected the object.
     * @param consumer        The consumer that will be called when the object is selected.
     * @param timerMessageKey The key of the message that will be sent to the player when the timer starts.
     */
    @Override
    public void selectElement(Player player, Consumer<T> consumer, String timerMessageKey) {
        if (buildFunction != null) {
            selectElement(player, consumer, timerMessageKey, buildFunction);
            return;
        }
        loadPage(getPage(), true);
        selectorManager.addEditorListener(player, BlobEditorListener.wise(player,
                consumer, timerMessageKey,
                this));
    }

    /**
     * Selects an object from the editor.
     *
     * @param player          The player that selected the object.
     * @param consumer        The consumer that will be called when the object is selected.
     * @param timerMessageKey The key of the message that will be sent to the player when the timer starts.
     * @param function        The function that will be called to customize the ItemStack.
     */
    @Override
    public void selectElement(Player player, Consumer<T> consumer, String timerMessageKey, Function<T, ItemStack> function) {
        loadCustomPage(getPage(), true, function);
        selectorManager.addSelectorListener(player, BlobSelectorListener.wise(player,
                consumer, timerMessageKey,
                this));
    }

    /**
     * Will attempt to remove the element from the collection
     * based on the loaded remove consumer.
     *
     * @param player The player to remove the element from.
     */
    public void removeElement(Player player) {
        if (removeConsumer == null)
            return;
        removeElement(player, removeConsumer);
    }

    /**
     * Removes an object from the editor.
     * The change will be reflected in the collection immediately.
     *
     * @param t The object to remove.
     */
    @Override
    public void remove(T t) {
        if (list == null)
            return;
        list.remove(t);
    }

    /**
     * @return the total pages
     */
    @Override
    public int totalPages(int itemsPerPage) {
        return (int) Math.ceil((double) getList().size() / (double) itemsPerPage);
    }

    /**
     * @return the total pages using getItemsPerPage() method
     */
    @Override
    public int getTotalPages() {
        int totalPages = totalPages(getItemsPerPage());
        if (totalPages == 0)
            totalPages = 1;
        return totalPages;
    }

    /**
     * Adds a new object to the editor.
     * The change will be reflected in the collection immediately.
     *
     * @param t The object to add.
     */
    @Override
    public void add(T t) {
        if (list == null)
            return;
        list.add(t);
    }

    /**
     * @return the list
     */
    @Override
    public List<T> getList() {
        if (collection != null) {
            return new ArrayList<>(collection);
        }
        return list;
    }

    /**
     * @param slot the slot to check
     * @return true if the slot is an add element button, false otherwise
     */
    public boolean isAddElementButton(int slot) {
        return Objects.requireNonNull(getSlots("Add-Element"),
                "Add-Element button not found").contains(slot);
    }

    /**
     * @param slot the slot to check
     * @return true if the slot is a remove element button, false otherwise
     */
    public boolean isRemoveElementButton(int slot) {
        return Objects.requireNonNull(getSlots("Remove-Element"),
                "Remove-Element button not found").contains(slot);
    }

    /**
     * Will attempt to add an element to the collection
     * through the following player.
     *
     * @param player The player to manage the action.
     */
    public void addElement(Player player) {
        if (addConsumer == null)
            return;
        addConsumer.accept(player);
    }

    /**
     * Will make the editor listen to the player
     * for actions such as navigating through pages,
     * adding and removing elements.
     * If anything of the previous is done,
     * the editor will adapt to the player for that
     * specific action.
     * If player selects an element, nothing will happen.
     *
     * @param player The player to manage the editor.
     */
    @Override
    public void manage(Player player, Consumer<T> removeConsumer) {
        if (buildFunction != null) {
            manage(player, buildFunction, removeConsumer);
            return;
        }
        loadPage(1, true);
        if (!selectorManager.addEditorListener(player,
                BlobEditorListener.wise(player,
                        input -> {
                            manageWithCache(player);
                        }, null,
                        this)))
            return;
        this.removeConsumer = removeConsumer;
    }

    /**
     * Will make the editor listen to the player
     * for actions such as navigating through pages,
     * adding and removing elements.
     * If anything of the previous is done,
     * the editor will adapt to the player for that
     * specific action.
     * If player selects an element, nothing will happen.
     *
     * @param player   The player to manage the editor.
     * @param function the function that loads ItemStacks in inventory
     */
    @Override
    public void manage(Player player, Function<T, ItemStack> function,
                       Consumer<T> removeConsumer) {
        loadCustomPage(1, true, function);
        if (!selectorManager.addEditorListener(player,
                BlobEditorListener.wise(player,
                        input -> {
                            manageWithCache(player);
                        }, null,
                        this)))
            return;
        setBuildFunction(function);
        this.removeConsumer = removeConsumer;
    }

    /**
     * Will make the editor listen to the player
     * using the cache for actions such as navigating through pages,
     * adding and removing elements.
     *
     * @param player The player to manage the editor.
     */
    public void manageWithCache(Player player) {
        if (removeConsumer == null)
            throw new IllegalStateException("removeConsumer is null");
        manage(player, removeConsumer);
    }

    /**
     * Will update the build function.
     * If null, selectElement will use the default build function.
     * If not null, selectElement will use the new build function.
     * It is automaitcally called when {@link #manage(Player, Function, Consumer)}
     * is called.
     *
     * @param buildFunction the new build function
     */
    public void setBuildFunction(Function<T, ItemStack> buildFunction) {
        this.buildFunction = buildFunction;
    }
}