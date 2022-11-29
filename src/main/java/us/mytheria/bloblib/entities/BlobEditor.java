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
import us.mytheria.bloblib.entities.message.BlobActionBar;
import us.mytheria.bloblib.managers.SelectorListenerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BlobEditor<T> extends VariableSelector<T> implements VariableEditor<T> {
    private final List<T> list;
    private final SelectorListenerManager selectorManager;

    public static <T> BlobEditor<T> build(BlobInventory blobInventory, UUID builderId,
                                          String dataType, VariableFiller<T> filler) {
        return new BlobEditor<>(blobInventory, builderId,
                dataType, filler);
    }

    private BlobEditor(BlobInventory blobInventory, UUID builderId,
                       String dataType, VariableFiller<T> filler) {
        super(blobInventory, builderId, dataType, filler);
        list = new ArrayList<>();
        selectorManager = BlobLib.getInstance().getSelectorManager();
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

    @SuppressWarnings("unchecked")
    public void removeElement(Player player) {
        selectorManager.addSelectorListener(player, BlobSelectorListener.build(player,
                () -> {
                    if (selectorManager.get(player).getInput() == null)
                        return;
                    T input = (T) selectorManager.get(player).getInput();
                    selectorManager.removeSelectorListener(player);
                    Bukkit.getScheduler().runTask(BlobLib.getInstance(), () -> {
                        if (player == null || !player.isOnline()) {
                            return;
                        }
                        list.remove(input);
                    });
                }, Collections.singletonList(new BlobActionBar(ChatColor.GRAY + "Click an element to remove it")),
                this));
    }

    public void addElement(T element) {
        list.add(element);
    }

    @Override
    public int totalPages(int itemsPerPage) {
        return (int) Math.ceil((double) list.size() / (double) itemsPerPage);
    }

    @Override
    public void add(T t) {
        list.add(t);
    }

    public List<T> getList() {
        return list;
    }
}