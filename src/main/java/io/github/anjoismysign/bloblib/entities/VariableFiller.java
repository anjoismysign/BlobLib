package io.github.anjoismysign.bloblib.entities;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author anjoismysign
 * A VariableFiller is an instance that was meant to fill an inventory
 * with items so that the user can select one of them. This was made
 * specifically for the BlobLib ObjectBuilder system.
 */
public interface VariableFiller<T> {
    /**
     * @param page         The page to get
     * @param itemsPerPage The amount of items per page
     * @return The items for the page
     */
    List<VariableValue<T>> page(int page, int itemsPerPage);

    /**
     * @param page         The page to get
     * @param itemsPerPage The amount of items per page
     * @param function     The function that will be used to create the itemstacks
     * @param list         The list of items to get the items from
     * @return The items for the page
     */
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

    /**
     * @param page The page to get
     * @return The total amount of pages
     */
    default List<VariableValue<T>> page(int page) {
        return page(page, 45);
    }

    /**
     * @param page     The page to get
     * @param list     The list of items to get the items from
     * @param function The function that will be used to create the itemstacks
     * @return The items for the page
     */
    default List<VariableValue<T>> customPage(int page, List<T> list, Function<T, ItemStack> function) {
        return customPage(page, 45, list, function);
    }

    /**
     * @param itemsPerPage The amount of items per page
     * @return The total amount of pages
     */
    int totalPages(int itemsPerPage);
}
