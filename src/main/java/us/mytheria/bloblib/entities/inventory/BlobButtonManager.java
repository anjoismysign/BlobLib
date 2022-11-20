package us.mytheria.bloblib.entities.inventory;

import me.anjoismysign.anjo.entities.uber.UberBoolean;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import us.mytheria.bloblib.entities.BlobMultiSlotable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class BlobButtonManager extends ButtonManager {
    /**
     * Builds a ButtonManager through the specified ConfigurationSection.
     * Uses HashMap to store buttons.
     *
     * @param section configuration section which contains all the buttons
     * @return a non abstract ButtonManager.
     */
    public static BlobButtonManager fromConfigurationSection(ConfigurationSection section) {
        BlobButtonManager blobButtonManager = new BlobButtonManager();
        blobButtonManager.add(section);
        return blobButtonManager;
    }

    /**
     * Builds a non abstract ButtonManager without any buttons stored yet.
     */
    public BlobButtonManager() {
        super(new HashMap<>(), new HashMap<>());
    }

    @Override
    public boolean contains(String key) {
        return getStringKeys().containsKey(key);
    }

    @Override
    public boolean contains(int key) {
        return getIntegerKeys().containsKey(key);
    }

    @Override
    public Set<Integer> get(String key) {
        return getStringKeys().get(key);
    }

    @Override
    public ItemStack get(int key) {
        return getIntegerKeys().get(key);
    }

    @Override
    public Collection<ItemStack> buttons() {
        return getIntegerKeys().values();
    }

    /**
     * adds all buttons inside a configuration section
     *
     * @param section configuration section which contains all the buttons
     * @return true if at least one button was succesfully added.
     * this is determined in case the being called after the first add call
     */
    @Override
    public boolean add(ConfigurationSection section) {
        Set<String> set = section.getKeys(false);
        UberBoolean madeChanges = new UberBoolean(false);
        set.forEach(s -> {
            if (contains(s))
                return;
            madeChanges.setValue(true);
            BlobMultiSlotable slotable = BlobMultiSlotable.fromConfigurationSection(section.getConfigurationSection(s), s);
            slotable.setInButtonManager(this);
        });
        return madeChanges.getValue();
    }
}
