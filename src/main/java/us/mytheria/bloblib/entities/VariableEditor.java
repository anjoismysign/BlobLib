package us.mytheria.bloblib.entities;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author anjoismysign
 * <p>
 * A VariableEditor can add and remove
 * objects from a collection inside a GUI.
 * It extends VariableFiller in order to manage
 * the inventory.
 */
public interface VariableEditor<T> extends VariableFiller<T> {
    /**
     * Adds a new object to the editor.
     * The change will be reflected in the collection immediately.
     *
     * @param t The object to add.
     */
    void add(T t);

    /**
     * Removes an object from the editor.
     * The change will be reflected in the collection immediately.
     *
     * @param t The object to remove.
     */
    void remove(T t);

    /**
     * Selects an object from the editor.
     *
     * @param player          The player that selected the object.
     * @param consumer        The consumer that will be called when the object is selected.
     * @param timerMessageKey The key of the message that will be sent to the player when the timer starts.
     */
    void selectElement(Player player, Consumer<T> consumer, @Nullable String timerMessageKey);

    /**
     * Selects an object from the editor.
     *
     * @param player   The player that selected the object.
     * @param consumer The consumer that will be called when the object is selected.
     */
    default void selectElement(Player player, Consumer<T> consumer) {
        selectElement(player, consumer, (String) null);
    }

    /**
     * Selects an object from the editor.
     *
     * @param player          The player that selected the object.
     * @param consumer        The consumer that will be called when the object is selected.
     * @param timerMessageKey The key of the message that will be sent to the player when the timer starts.
     * @param function        The function that will be called to customize the ItemStack.
     */
    void selectElement(Player player, Consumer<T> consumer, @Nullable String timerMessageKey, Function<T, ItemStack> function);

    /**
     * Selects an object from the editor.
     *
     * @param player   The player that selected the object.
     * @param consumer The consumer that will be called when the object is selected.
     * @param function The function that will be called to customize the ItemStack.
     */
    default void selectElement(Player player, Consumer<T> consumer, Function<T, ItemStack> function) {
        selectElement(player, consumer, null, function);
    }

    /**
     * Open a menu to the player, so they can choose an element to remove.
     * 'Editor.Remove' will be sent to the player while the timer is running.
     *
     * @param player   The player that removed the object.
     * @param consumer The consumer that will be called when the object is removed.
     */
    default void removeElement(Player player, Consumer<T> consumer) {
        selectElement(player, input -> {
            remove(input);
            consumer.accept(input);
        }, "Editor.Remove");
    }

    /**
     * Open a menu to the player, so they can choose an element to remove.
     * 'Editor.Remove' will be sent to the player while the timer is running.
     * The function is for customizing the ItemStack that represents each object.
     *
     * @param player   The player that removed the object.
     * @param consumer The consumer that will be called when the object is removed.
     * @param function The function that will be called to customize the ItemStack.
     */
    default void removeElement(Player player, Consumer<T> consumer,
                               Function<T, ItemStack> function) {
        selectElement(player, input -> {
            remove(input);
            consumer.accept(input);
        }, "Editor.Remove", function);
    }
}