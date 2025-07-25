package io.github.anjoismysign.bloblib.entities.inventory;

import io.github.anjoismysign.anjo.entities.Uber;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

/**
 * @author anjoismysign
 * A BlobButtonManager is a smart tool for GUIs.
 * It allows you to store buttons as key-value pairs into which
 * you can store a single ItemStack to multiple slots of an inventory.
 * It also allows you to manage all the slots that this ItemStack is currently
 * stored in. One feature of this class is that it allows you parse a ButtonManager
 * from a YAML file.
 * <p>
 * It handles the above through HashMaps giving it a O(1) time complexity.
 */
public class MetaBlobButtonManager extends ButtonManager<MetaInventoryButton> {
    /**
     * Builds a ButtonManager through the specified ConfigurationSection.
     * Uses HashMap to store buttons.
     *
     * @param section configuration section which contains all the buttons
     * @return a non abstract ButtonManager.
     */
    public static MetaBlobButtonManager fromConfigurationSection(@NotNull ConfigurationSection section,
                                                                 @NotNull String locale) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(locale);
        MetaBlobButtonManager blobButtonManager = new MetaBlobButtonManager();
        blobButtonManager.add(section, locale);
        return blobButtonManager;
    }

    /**
     * Builds a non abstract ButtonManager without any buttons stored yet.
     */
    public MetaBlobButtonManager() {
        this(new ButtonManagerData<>(new HashMap<>(),
                new HashMap<>()));
    }

    private MetaBlobButtonManager(ButtonManagerData<MetaInventoryButton> buttonManagerData) {
        super(buttonManagerData);
    }

    @Override
    public MetaBlobButtonManager copy() {
        return new MetaBlobButtonManager(new ButtonManagerData<>(copyStringKeys(),
                copyIntegerKeys()));
    }

    /**
     * Checks if the specified key is stored in this ButtonManager
     *
     * @param key the key to check
     * @return true if the specified key is stored in this ButtonManager
     */
    @Override
    public boolean contains(String key) {
        return getStringKeys().containsKey(key);
    }

    /**
     * Checks if the specified slot is stored in this ButtonManager
     *
     * @param slot the slot to check
     * @return true if the specified slot is stored in this ButtonManager
     */
    @Override
    public boolean contains(int slot) {
        return getIntegerKeys().containsKey(slot);
    }

    /**
     * Gets all buttons stored in this ButtonManager that belong to the specified key
     *
     * @param key the key of the buttons
     * @return all buttons stored in this ButtonManager that belong to the specified key
     * null if the key is not stored in this ButtonManager
     */
    @Override
    public Set<Integer> get(String key) {
        if (contains(key)) {
            return getStringKeys().get(key).getSlots();
        }
        return null;
    }

    /**
     * returns an ItemStack stored in this ButtonManager
     *
     * @param slot the slot of the ItemStack
     * @return the ItemStack stored in this ButtonManager
     */
    @Override
    public ItemStack get(int slot) {
        return getIntegerKeys().get(slot);
    }

    /**
     * returns all ItemStacks stored in this ButtonManager
     *
     * @return all ItemStacks in this ButtonManager
     */
    @Override
    public Collection<ItemStack> buttons() {
        return getIntegerKeys().values();
    }

    /**
     * adds all buttons inside a configuration section through parsing.
     * uses the default locale
     *
     * @param section configuration section which contains all the buttons
     * @return true if at least one button was succesfully added.
     * this is determined in case the being called after the first add call
     */
    public boolean add(ConfigurationSection section) {
        return add(section, "en_us");
    }

    /**
     * returns the keys of all buttons stored in this ButtonManager
     *
     * @return keys of all buttons stored in this ButtonManager
     */
    @Override
    public Collection<String> keys() {
        return getStringKeys().keySet();
    }

    /**
     * adds all buttons inside a configuration section through parsing
     *
     * @param section configuration section which contains all the buttons
     * @return true if at least one button was succesfully added.
     * this is determined in case the being called after the first add call
     */
    public boolean add(ConfigurationSection section, String locale) {
        Set<String> set = section.getKeys(false);
        Uber<Boolean> madeChanges = new Uber<>(false);
        set.stream().filter(key -> !contains(key)).forEach(key -> {
            madeChanges.talk(true);
            MetaBlobMultiSlotable slotable = MetaBlobMultiSlotable
                    .read(section.getConfigurationSection(key), key, locale);
            slotable.setInButtonManager(this);
        });
        return madeChanges.thanks();
    }
}
