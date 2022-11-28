package us.mytheria.bloblib.managers.fillermanager;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mytheria.bloblib.entities.VariableFiller;
import us.mytheria.bloblib.entities.VariableValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class SuperBlobFiller<T> implements VariableFiller<T> {
    private final Collection<T> values;
    private final Function<T, ItemStack> function;

    public static <T> SuperBlobFiller<T> build(Collection values, Function<T, ItemStack> function) {
        return new SuperBlobFiller<T>(values, function);
    }

    private SuperBlobFiller(Collection<T> values, Function<T, ItemStack> function) {
        this.values = values;
        this.function = function;
    }

    @Override
    public List<VariableValue<T>> page(int page, int itemsPerPage) {
        int start = (page - 1) * itemsPerPage;
        int end = start + (itemsPerPage);
        ArrayList<T> list = new ArrayList<>(this.values);
        ArrayList<VariableValue<T>> values = new ArrayList<>();
        for (int i = start; i < end; i++) {
            T t;
            try {
                t = list.get(i);
                ItemStack itemStack = function.apply(t);
                if (itemStack == null) {
                    itemStack = new ItemStack(Material.BARRIER);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName(ChatColor.DARK_RED + "Error in SuperBlobFiller's function");
                    itemStack.setItemMeta(itemMeta);
                }
                values.add(new VariableValue<>(itemStack, t));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        return values;
    }

    @Override
    public int totalPages(int itemsPerPage) {
        return (int) Math.ceil((double) values.size() / (double) itemsPerPage);
    }
}