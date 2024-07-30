package us.mytheria.bloblib.entities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.inventory.BlobInventory;
import us.mytheria.bloblib.entities.inventory.VariableSelector;
import us.mytheria.bloblib.entities.listeners.BlobSelectorListener;
import us.mytheria.bloblib.entities.listeners.EditorActionType;
import us.mytheria.bloblib.managers.SelectorListenerManager;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
                                            String dataType, Collection<T> collection,
                                            @Nullable Consumer<Player> onReturn) {
        return new BlobSelector<>(blobInventory, builderId,
                dataType, collection, onReturn);
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
                                            String dataType,
                                            @Nullable Consumer<Player> onReturn) {
        return new BlobSelector<>(blobInventory, builderId,
                dataType, onReturn);
    }

    /**
     * Creates a new BlobEditor
     *
     * @param <T>       the type of the collection
     * @param builderId the id of the builder
     * @param dataType  the data type of the editor
     * @return the new BlobEditor
     */
    public static <T> BlobSelector<T> DEFAULT(UUID builderId, String dataType,
                                              @Nullable Consumer<Player> onReturn) {
        Player player = Objects.requireNonNull(Bukkit.getPlayer(builderId));
        return new BlobSelector<>(VariableSelector.DEFAULT(player), builderId,
                dataType, onReturn);
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
                                                           Collection<T> collection,
                                                           @Nullable Consumer<Player> onReturn) {
        Player player = Objects.requireNonNull(Bukkit.getPlayer(builderId));
        return new BlobSelector<>(VariableSelector.DEFAULT(player), builderId,
                dataType, collection, onReturn);
    }

    protected BlobSelector(BlobInventory blobInventory, UUID builderId,
                           String dataType, @Nullable Consumer<Player> consumer) {
        super(blobInventory, builderId, dataType, null, consumer);
        this.selectorManager = BlobLib.getInstance().getSelectorManager();
        this.list = new ArrayList<>();
        this.collection = null;
    }

    protected BlobSelector(BlobInventory blobInventory, UUID builderId,
                           String dataType, Collection<T> collection,
                           @Nullable Consumer<Player> consumer) {
        super(Objects.requireNonNull(blobInventory, "'blobInventory' cannot be null"),
                Objects.requireNonNull(builderId, "'builderId' cannot be null"),
                Objects.requireNonNull(dataType, "'dataType' cannot be null"),
                null, consumer);
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

    public void selectElement(Player player, Consumer<T> consumer, String timerMessageKey) {
        loadPage(getPage(), true);
        selectorManager.addSelectorListener(player, BlobSelectorListener.wise(player,
                consumer, timerMessageKey,
                this, null));
    }

    /**
     * @deprecated use {@link #selectElement(Player, Consumer, String, Function, Supplier, Consumer, String)}
     */
    @Deprecated
    public void selectElement(Player player,
                              Consumer<T> consumer,
                              String timerMessageKey,
                              Function<T, ItemStack> function,
                              Supplier<Collection<T>> selectorList) {
        selectElement(player, consumer, timerMessageKey, function, selectorList, null, null);
    }

    public void selectElement(Player player,
                              Consumer<T> consumer,
                              String timerMessageKey,
                              Function<T, ItemStack> function,
                              Supplier<Collection<T>> selectorList,
                              @Nullable Consumer<Player> onClose,
                              @Nullable String clickSound) {
        loadCustomPage(getPage(), true, function);
        setLoadFunction(function);
        setCollectionSupplier(selectorList);
        BlobSelectorListener<T> listener = BlobSelectorListener.wise(player,
                consumer, timerMessageKey,
                this, onClose);
        if (clickSound != null)
            listener.setClickSoundReference(clickSound);
        selectorManager.addSelectorListener(player, listener);
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
    @Override
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