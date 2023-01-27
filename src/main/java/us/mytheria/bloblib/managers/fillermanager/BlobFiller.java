package us.mytheria.bloblib.managers.fillermanager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mytheria.bloblib.entities.VariableFiller;
import us.mytheria.bloblib.entities.VariableValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @param <T> The type
 * @author anjoismysign
 * <p>
 * A BlobFiller is an instance of VariableFiller.
 */
public class BlobFiller<T> implements VariableFiller<T> {
    private final Collection<T> values;
    private final Material material;

    /**
     * Builds a BlobFiller
     *
     * @param values   The values
     * @param material The material
     * @param <T>      The type
     * @return A BlobFiller
     */
    public static <T> BlobFiller<T> build(Collection<T> values, Material material) {
        return new BlobFiller<>(values, material);
    }

    private BlobFiller(Collection<T> values, Material material) {
        this.values = values;
        this.material = material;
    }

    /**
     * Returns a list of VariableValues
     *
     * @param page         The page
     * @param itemsPerPage The amount of items per page
     * @return A list of VariableValues
     */
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
                ItemStack itemStack = new ItemStack(material);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ChatColor.GOLD + t.toString());
                itemStack.setItemMeta(itemMeta);
                values.add(new VariableValue<>(itemStack, t));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        return values;
    }

    /**
     * Returns the total amount of pages
     *
     * @param itemsPerPage The amount of items per page
     * @return The total amount of pages
     */
    @Override
    public int totalPages(int itemsPerPage) {
        return (int) Math.ceil((double) values.size() / (double) itemsPerPage);
    }
}