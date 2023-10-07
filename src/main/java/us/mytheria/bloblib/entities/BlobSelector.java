package us.mytheria.bloblib.entities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.inventory.BlobInventory;
import us.mytheria.bloblib.entities.inventory.VariableSelector;
import us.mytheria.bloblib.entities.listeners.BlobSelectorListener;
import us.mytheria.bloblib.entities.listeners.EditorActionType;
import us.mytheria.bloblib.managers.SelectorListenerManager;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author anjoismysign
 * A BlobEditor is a VariableEditor that can be used to edit a collection.
 */
public class BlobSelector<T> extends VariableSelector<T> implements VariableFiller<T> {
    private final List<T> list;
    private final Collection<T> collection;
    private final SelectorListenerManager selectorManager;

    /**
     * Creates a new BlobEditor passing a BlobInventory for VariableSelector
     * and injects a collection.
     *
     * @param <T>           the type of the collection
     * @param blobInventory the BlobInventory
     * @param builderId     the id of the builder
     * @param dataType      the data type of the editor
     * @param collection    the collection to inject
     * @return the new BlobEditor
     */
    public static <T> BlobSelector<T> build(BlobInventory blobInventory, UUID builderId,
                                            String dataType, Collection<T> collection) {
        return new BlobSelector<>(blobInventory, builderId,
                dataType, collection);
    }


    /**
     * Creates a new BlobEditor passing a BlobInventory for VariableSelector
     *
     * @param <T>           the type of the collection
     * @param blobInventory the BlobInventory
     * @param builderId     the id of the builder
     * @param dataType      the data type of the editor
     * @return the new BlobEditor
     */
    public static <T> BlobSelector<T> build(BlobInventory blobInventory, UUID builderId,
                                            String dataType) {
        return new BlobSelector<>(blobInventory, builderId,
                dataType);
    }

    /**
     * Creates a new BlobEditor
     *
     * @param <T>       the type of the collection
     * @param builderId the id of the builder
     * @param dataType  the data type of the editor
     * @return the new BlobEditor
     */
    public static <T> BlobSelector<T> DEFAULT(UUID builderId, String dataType) {
        Player player = Objects.requireNonNull(Bukkit.getPlayer(builderId));
        return new BlobSelector<>(VariableSelector.DEFAULT(player), builderId,
                dataType);
    }

    /**
     * Creates a new BlobEditor passing specific collection.
     *
     * @param <T>        the type of the collection
     * @param builderId  the id of the builder
     * @param dataType   the data type of the editor
     * @param collection the collection to edit
     * @return the new BlobEditor
     */
    public static <T> BlobSelector<T> COLLECTION_INJECTION(UUID builderId, String dataType,
                                                           Collection<T> collection) {
        Player player = Objects.requireNonNull(Bukkit.getPlayer(builderId));
        return new BlobSelector<>(VariableSelector.DEFAULT(player), builderId,
                dataType, collection);
    }

    protected BlobSelector(BlobInventory blobInventory, UUID builderId,
                           String dataType) {
        super(blobInventory, builderId, dataType, null);
        this.selectorManager = BlobLib.getInstance().getSelectorManager();
        this.list = new ArrayList<>();
        this.collection = null;
    }

    protected BlobSelector(BlobInventory blobInventory, UUID builderId,
                           String dataType, Collection<T> collection) {
        super(Objects.requireNonNull(blobInventory, "'blobInventory' cannot be null"),
                Objects.requireNonNull(builderId, "'builderId' cannot be null"),
                Objects.requireNonNull(dataType, "'dataType' cannot be null"),
                null);
        this.selectorManager = BlobLib.getInstance().getSelectorManager();
        this.collection = collection;
        this.list = null;
    }

    /**
     * Loads the first page
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
     * loads the page with the given page number
     *
     * @param page     the page number
     * @param refill   if the background should be refilled
     * @param function the function to apply
     */
    public void loadCustomPage(int page, boolean refill, Function<T, ItemStack> function) {
        if (page < 1) {
            return;
        }
        if (getTotalPages() < page) {
            return;
        }
        if (refill)
            refillButton("White-Background");
        clearValues();
        List<VariableValue<T>> values = this.customPage(page, getItemsPerPage(), function);
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
     * returns specific page with provided function without loading
     *
     * @param page         the page
     * @param itemsPerPage the items per page
     * @param function     the function to apply
     * @return the list of values
     */
    public List<VariableValue<T>> customPage(int page, int itemsPerPage, Function<T, ItemStack> function) {
        int start = (page - 1) * itemsPerPage;
        int end = start + (itemsPerPage);
        ArrayList<VariableValue<T>> values = new ArrayList<>();
        for (int i = start; i < end; i++) {
            T get;
            try {
                get = getList().get(i);
                ItemStack itemStack = function.apply(get);
                if (itemStack == null)
                    continue;
                values.add(new VariableValue<>(itemStack, get));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        return values;
    }

    public void selectElement(Player player, Consumer<T> consumer, String timerMessageKey) {
        loadPage(getPage(), true);
        selectorManager.addSelectorListener(player, BlobSelectorListener.wise(player,
                consumer, timerMessageKey,
                this));
    }

    public void selectElement(Player player, Consumer<T> consumer, String timerMessageKey, Function<T, ItemStack> function) {
        loadCustomPage(getPage(), true, function);
        selectorManager.addSelectorListener(player, BlobSelectorListener.wise(player,
                consumer, timerMessageKey,
                this));
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
     * @return the list
     */
    public List<T> getList() {
        if (collection != null) {
            return new ArrayList<>(collection);
        }
        return list;
    }

    public void process(EditorActionType input, Player player) {
        switch (input) {
            case NEXT_PAGE -> {
                nextPage();
            }
            case PREVIOUS_PAGE -> {
                previousPage();
            }
            default -> {
                throw new IllegalStateException("Unexpected value: " + input + "\n" +
                        "Contact BlobLib developer.");
            }
        }
    }

    public void process(EditorActionType input) {
        Player player = getPlayer();
        if (player == null)
            return;
        process(input, player);
    }
}