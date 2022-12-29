package us.mytheria.bloblib.entities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.inventory.BlobInventory;
import us.mytheria.bloblib.entities.inventory.VariableSelector;
import us.mytheria.bloblib.entities.listeners.BlobSelectorListener;
import us.mytheria.bloblib.entities.message.BlobActionbarMessage;
import us.mytheria.bloblib.managers.SelectorListenerManager;

import java.util.*;
import java.util.function.Function;

public class BlobEditor<T> extends VariableSelector<T> implements VariableEditor<T> {
    private final List<T> list;
    private final Collection<T> collection;
    private final SelectorListenerManager selectorManager;

    public static <T> BlobEditor<T> build(BlobInventory blobInventory, UUID builderId,
                                          String dataType) {
        return new BlobEditor<>(blobInventory, builderId,
                dataType);
    }

    public static <T> BlobEditor<T> DEFAULT(UUID builderId, String dataType) {
        return new BlobEditor<>(VariableSelector.DEFAULT(), builderId,
                dataType);
    }

    public static <T> BlobEditor<T> DEFAULT_ITEMSTACKREADER(UUID builderId, String dataType) {
        return new BlobEditor<>(VariableSelector.DEFAULT_ITEMSTACKREADER(), builderId,
                dataType);
    }

    public static <T> BlobEditor<T> DEFAULT_ITEMSTACKREADER(UUID builderId, String dataType, Collection<T> collection) {
        return new BlobEditor<>(VariableSelector.DEFAULT_ITEMSTACKREADER(), builderId,
                dataType, collection);
    }

    private BlobEditor(BlobInventory blobInventory, UUID builderId,
                       String dataType) {
        super(blobInventory, builderId, dataType, null);
        list = new ArrayList<>();
        collection = null;
        selectorManager = BlobLib.getInstance().getSelectorManager();
    }

    private BlobEditor(BlobInventory blobInventory, UUID builderId,
                       String dataType, Collection<T> collection) {
        super(blobInventory, builderId, dataType, null);
        this.collection = collection;
        list = null;
        selectorManager = BlobLib.getInstance().getSelectorManager();
    }

    @Override
    public void loadInConstructor() {
    }

    @Override
    public void loadPage(int page, boolean refill) {
        if (page < 1) {
            return;
        }
        if (getTotalPages() < page) {
            return;
        }
        if (refill)
            refillButton("White-Background");
        clearValues();
        List<VariableValue<T>> values = this.page(page, getItemsPerPage());
        for (int i = 0; i < values.size(); i++) {
            setValue(i, values.get(i));
        }
    }

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

    public List<VariableValue<T>> customPage(int page, int itemsPerPage, Function<T, ItemStack> function) {
        int start = (page - 1) * itemsPerPage;
        int end = start + (itemsPerPage);
        ArrayList<VariableValue<T>> values = new ArrayList<>();
        for (int i = start; i < end; i++) {
            T get;
            try {
                get = getList().get(i);
                values.add(new VariableValue<>(function.apply(get), get));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        return values;
    }

    public void remove(T t) {
        list.remove(t);
    }

    @SuppressWarnings("unchecked")
    public void removeElement(Player player, Runnable onRemove) {
        loadPage(getPage(), true);
        selectorManager.addSelectorListener(player, BlobSelectorListener.build(player,
                () -> {
                    if (selectorManager.get(player).getInput() == null) {
                        selectorManager.removeSelectorListener(player);
                        onRemove.run();
                        return;
                    }
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                    T input = (T) selectorManager.get(player).getInput();
                    selectorManager.removeSelectorListener(player);
                    Bukkit.getScheduler().runTask(BlobLib.getInstance(), () -> {
                        if (player == null || !player.isOnline()) {
                            return;
                        }
                        remove(input);
                        onRemove.run();
                    });
                }, Collections.singletonList(new BlobActionbarMessage(ChatColor.GRAY + "Click an element to remove it")),
                this));
    }

    public void removeElement(Player player, Runnable onRemove, Function<T, ItemStack> function) {
        loadCustomPage(getPage(), true, function);
        selectorManager.addSelectorListener(player, BlobSelectorListener.build(player,
                () -> {
                    if (selectorManager.get(player).getInput() == null) {
                        selectorManager.removeSelectorListener(player);
                        onRemove.run();
                        return;
                    }
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                    T input = (T) selectorManager.get(player).getInput();
                    selectorManager.removeSelectorListener(player);
                    Bukkit.getScheduler().runTask(BlobLib.getInstance(), () -> {
                        if (player == null || !player.isOnline()) {
                            return;
                        }
                        remove(input);
                        onRemove.run();
                    });
                }, Collections.singletonList(new BlobActionbarMessage(ChatColor.GRAY + "Click an element to remove it")),
                this));
    }

    public void addElement(T element) {
        list.add(element);
    }

    @Override
    public int totalPages(int itemsPerPage) {
        return (int) Math.ceil((double) getList().size() / (double) itemsPerPage);
    }

    @Override
    public int getTotalPages() {
        int totalPages = totalPages(getItemsPerPage());
        if (totalPages == 0)
            totalPages = 1;
        return totalPages;
    }

    @Override
    public void add(T t) {
        list.add(t);
    }

    public List<T> getList() {
        if (collection != null) {
            return new ArrayList<>(collection);
        }
        return list;
    }
}