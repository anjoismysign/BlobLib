package us.mytheria.bloblib.entities;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface VariableFiller<T> {
    List<VariableValue<T>> page(int page, int itemsPerPage);

    default List<VariableValue<T>> customPage(int page, int itemsPerPage, List<T> list, Function<T, ItemStack> function) {
        int start = (page - 1) * itemsPerPage;
        int end = start + (itemsPerPage);
        ArrayList<VariableValue<T>> values = new ArrayList<>();
        for (int i = start; i < end; i++) {
            T get;
            try {
                get = list.get(i);
                ItemStack itemStack = function.apply(get);
                values.add(new VariableValue<>(itemStack, get));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        return values;
    }

    default List<VariableValue<T>> page(int page) {
        return page(page, 45);
    }

    default List<VariableValue<T>> customPage(int page, List<T> list, Function<T, ItemStack> function) {
        return customPage(page, 45, list, function);
    }

    int totalPages(int itemsPerPage);
}
