package us.mytheria.bloblib.entities;

import org.bukkit.inventory.ItemStack;
import us.mytheria.bloblib.entities.inventory.ButtonManagerMethods;

import java.util.Map;
import java.util.Set;

public abstract class MetaButtonManager implements ButtonManagerMethods {
    private Map<String, MetaInventoryButton> stringKeys;
    private Map<Integer, ItemStack> integerKeys;

    public MetaButtonManager(Map<String, MetaInventoryButton> stringKeys,
                             Map<Integer, ItemStack> integerKeys) {
        this.stringKeys = stringKeys;
        this.integerKeys = integerKeys;
    }

    public MetaButtonManager() {
    }

    public Map<Integer, ItemStack> getIntegerKeys() {
        return integerKeys;
    }

    public Map<String, MetaInventoryButton> getStringKeys() {
        return stringKeys;
    }

    public void setIntegerKeys(Map<Integer, ItemStack> integerKeys) {
        this.integerKeys = integerKeys;
    }

    public void setStringKeys(Map<String, MetaInventoryButton> stringKeys) {
        this.stringKeys = stringKeys;
    }

    public Set<Integer> getSlots(String key) {
        if (stringKeys.containsKey(key)) {
            return stringKeys.get(key).getSlots();
        }
        throw new IllegalArgumentException("MetaInventoryButton with key " + key + " does not exist");
    }

    public MetaInventoryButton getButton(String key) {
        return stringKeys.get(key);
    }
}
