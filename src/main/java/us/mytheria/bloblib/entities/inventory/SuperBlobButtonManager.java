package us.mytheria.bloblib.entities.inventory;

import me.anjoismysign.anjo.entities.uber.UberBoolean;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import us.mytheria.bloblib.entities.CommandMultiSlotable;
import us.mytheria.bloblib.entities.SuperInventoryButton;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class SuperBlobButtonManager extends ButtonManager {
    private HashMap<String, String> buttonCommands;

    public static SuperBlobButtonManager smartFromConfigurationSection(ConfigurationSection section) {
        SuperBlobButtonManager superBlobButtonManager = new SuperBlobButtonManager();
        superBlobButtonManager.read(section);
        return superBlobButtonManager;
    }

    /**
     * Builds a SuperButtonManager through the specified ConfigurationSection.
     * Uses HashMap to store buttons.
     *
     * @param section configuration section which contains all the buttons
     * @return a non abstract ButtonManager.
     */
    public static SuperBlobButtonManager fromConfigurationSection(ConfigurationSection section) {
        SuperBlobButtonManager blobButtonManager = new SuperBlobButtonManager();
        blobButtonManager.add(section);
        return blobButtonManager;
    }

    /**
     * Builds a non abstract ButtonManager without any buttons stored yet.
     */
    public SuperBlobButtonManager() {
        super(new HashMap<>(), new HashMap<>());
        buttonCommands = new HashMap<>();
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
    public Collection<String> keys() {
        return getStringKeys().keySet();
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
            CommandMultiSlotable slotable = CommandMultiSlotable.fromConfigurationSection(section.getConfigurationSection(s), s);
            slotable.setInSuperBlobButtonManager(this);
        });
        return madeChanges.getValue();
    }

    public boolean read(ConfigurationSection section) {
        Set<String> set = section.getKeys(false);
        UberBoolean madeChanges = new UberBoolean(false);
        set.forEach(s -> {
            if (contains(s))
                return;
            madeChanges.setValue(true);
            CommandMultiSlotable slotable = CommandMultiSlotable.read(section.getConfigurationSection(s), s);
            slotable.setInSuperBlobButtonManager(this);
        });
        return madeChanges.getValue();
    }

    @Override
    public void addCommand(String key, String command) {
        buttonCommands.put(key, command);
    }

    @Override
    public String getCommand(String key) {
        return buttonCommands.get(key);
    }

    @Override
    public SuperInventoryButton getSuperButton(String key) {
        String command = getCommand(key);
        return SuperInventoryButton.fromInventoryButton(getButton(key), command,
                command != null);
    }
}
