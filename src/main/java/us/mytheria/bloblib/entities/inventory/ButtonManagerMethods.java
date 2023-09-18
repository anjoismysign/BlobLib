package us.mytheria.bloblib.entities.inventory;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Set;

public interface ButtonManagerMethods {
    /**
     * @param key the key of the InventoryButton
     * @return true if the specified key points to a stored InventoryButton in this ButtonManager
     */
    boolean contains(String key);

    /**
     * @param slot the key of the ItemStack
     * @return true if the specified key is stored in this ButtonManager
     */
    boolean contains(int slot);

    /**
     * @param key the key of the InventoryButton
     * @return the slots in which the ItemStack is stored
     */
    Set<Integer> get(String key);

    /**
     * @param slot the slot of the ItemStack
     * @return the ItemStack stored in this ButtonManager
     */
    ItemStack get(int slot);

    /**
     * @return all ItemStacks in this ButtonManager
     */
    Collection<ItemStack> buttons();

    /**
     * adds all buttons inside a configuration section through parsing
     *
     * @param section the configuration section to read from
     * @return true if all buttons were added successfully
     */
    boolean add(ConfigurationSection section);

    /**
     * @return a set of all the keys stored in this ButtonManager
     */
    Collection<String> keys();

    /**
     * @param key the key of the InventoryButton
     * @return the InventoryButton stored in this ButtonManager
     */
    InventoryButton getButton(String key);
}
