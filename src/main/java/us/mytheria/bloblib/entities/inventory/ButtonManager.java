package us.mytheria.bloblib.entities.inventory;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class ButtonManager<T extends InventoryButton> implements ButtonManagerMethods {
    private Map<String, T> stringKeys;
    private Map<Integer, ItemStack> integerKeys;

    public ButtonManager(ButtonManagerData<T> buttonManagerData) {
        this.stringKeys = new HashMap<>(buttonManagerData.inventoryButtons());
        this.integerKeys = new HashMap<>(buttonManagerData.itemStackMap());
    }

    public ButtonManager() {
    }

    public Map<Integer, ItemStack> getIntegerKeys() {
        return integerKeys;
    }

    public Map<String, T> getStringKeys() {
        return stringKeys;
    }

    public void setIntegerKeys(Map<Integer, ItemStack> integerKeys) {
        this.integerKeys = integerKeys;
    }

    public void setStringKeys(Map<String, T> stringKeys) {
        this.stringKeys = stringKeys;
    }

    public Set<Integer> getSlots(String key) {
        return get(key);
    }

    /**
     * Checks if the InventoryButton for the specified key is stored in this ButtonManager
     *
     * @param key the key of the InventoryButton
     * @return the InventoryButton for the specified key is stored in this ButtonManager
     * null if the InventoryButton for the specified key is not stored in this ButtonManager
     */
    @Override
    @Nullable
    public T getButton(String key) {
        return getStringKeys().get(key);
    }

    /**
     * @return all InventoryButtons stored in this ButtonManager
     */
    @NotNull
    public Collection<T> getAllButtons() {
        return getStringKeys().values();
    }

    /**
     * Will copy/clone the ButtonManager to a new instance
     *
     * @return the new instance of the ButtonManager
     */
    public abstract ButtonManager<T> copy();

    /**
     * Copies stringKeys map used in ButtonManager constructor
     *
     * @return a copy of the stringKeys map
     */
    @SuppressWarnings("unchecked")
    public Map<String, T> copyStringKeys() {
        Map<String, T> stringKeys = new HashMap<>();
        this.stringKeys.forEach((key, value) -> {
            stringKeys.put(key, (T) value.copy());
        });
        return stringKeys;
    }

    /**
     * Copies integerKeys map used in ButtonManager constructor
     *
     * @return a copy of the integerKeys map
     */
    public Map<Integer, ItemStack> copyIntegerKeys() {
        Map<Integer, ItemStack> map = new HashMap<>();
        this.integerKeys.forEach((key, value) -> map.put(key, new ItemStack(value)));
        return map;
    }
}
