package us.mytheria.bloblib.entities;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author anjoismysign
 * <p>
 * A VariableEditor can add and remove
 * objects of a collection inside a GUI.
 */
public interface VariableEditor<T> extends VariableFiller<T> {
    /**
     * Adds a new object to the editor.
     *
     * @param t The object to add.
     */
    void add(T t);

    /**
     * Open a menu to the player, so they can choose an element to remove.
     *
     * @param player   The player that removed the object.
     * @param consumer The consumer that will be called when the object is removed.
     */
    void removeElement(Player player, Consumer<T> consumer);

    /**
     * Open a menu to the player, so they can choose an element to remove.
     * The function is for customizing the ItemStack that represents each object.
     *
     * @param player   The player that removed the object.
     * @param consumer The consumer that will be called when the object is removed.
     * @param function The function that will be called to customize the ItemStack.
     */
    void removeElement(Player player, Consumer<T> consumer,
                       Function<T, ItemStack> function);
}