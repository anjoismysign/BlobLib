package us.mytheria.bloblib.entities;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.mytheria.bloblib.entities.inventory.ButtonManager;
import us.mytheria.bloblib.itemstack.ItemStackReader;
import us.mytheria.bloblib.objects.SerializableItem;

import java.util.HashSet;
import java.util.Set;

public class BlobMultiSlotable extends MultiSlotable {
    private String key;

    public static BlobMultiSlotable fromConfigurationSection(ConfigurationSection section, String key) {
        ItemStack itemStack = SerializableItem.fromConfigurationSection(section.getConfigurationSection("ItemStack"));
        HashSet<Integer> list = new HashSet<>();
        String read = section.getString("Slot", "-1");
        String[] slots = read.split(",");
        if (slots.length != 1) {
            for (String slot : slots) {
                add(list, slot, section.getName());
            }
        } else {
            add(list, read, section.getName());
        }
        return new BlobMultiSlotable(list, itemStack, key);
    }

    public static BlobMultiSlotable read(ConfigurationSection section, String key) {
        ConfigurationSection itemStackSection = section.getConfigurationSection("ItemStack");
        if (itemStackSection == null) {
            Bukkit.getLogger().severe("ItemStack section is null for " + key);
            return null;
        }
        ItemStack itemStack = ItemStackReader.read(itemStackSection).build();
        HashSet<Integer> list = new HashSet<>();
        String read = section.getString("Slot", "-1");
        String[] slots = read.split(",");
        if (slots.length != 1) {
            for (String slot : slots) {
                add(list, slot, section.getName());
            }
        } else {
            add(list, read, section.getName());
        }
        return new BlobMultiSlotable(list, itemStack, key);
    }

    public BlobMultiSlotable(Set<Integer> slots, ItemStack itemStack, String key) {
        super(slots, itemStack);
        this.key = key;
    }

    public void setInInventory(Inventory inventory) {
        for (Integer slot : getSlots()) {
            inventory.setItem(slot, getItemStack());
        }
    }

    public void setInButtonManager(ButtonManager buttonManager) {
        buttonManager.getStringKeys().put(key, this.getSlots());
        for (Integer slot : getSlots()) {
            buttonManager.getIntegerKeys().put(slot, getItemStack());
        }
    }

    private static void add(Set<Integer> set, String raw, String sectionName) {
        String[] split = raw.split("-");
        switch (split.length) {
            case 1 -> {
                int slot = Integer.parseInt(split[0]);
                if (slot < 0) {
                    Bukkit.getLogger().info(sectionName + " got a slot that's is smaller than 0.");
                    Bukkit.getLogger().info("This is not possible in an inventory so it was set");
                    Bukkit.getLogger().info("to '0' which is default.");
                    slot = 0;
                }
                set.add(slot);
            }
            case 2 -> {
                int start = 0;
                try {
                    start = Integer.parseInt(split[0]);
                } catch (NumberFormatException e) {
                    Bukkit.getLogger().info(sectionName + " got a slot that's not a number");
                    Bukkit.getLogger().info("This is not possible in an inventory so it was set");
                    Bukkit.getLogger().info("to '0' which is default.");
                }
                int end = 0;
                try {
                    end = Integer.parseInt(split[1]);
                } catch (NumberFormatException e) {
                    Bukkit.getLogger().info(sectionName + " got a slot that's not a number");
                    Bukkit.getLogger().info("This is not possible in an inventory so it was set");
                    Bukkit.getLogger().info("to '0' which is default.");
                }
                for (int i = start; i <= end; i++) {
                    set.add(i);
                }
            }
            default -> {
                Bukkit.getLogger().info("Invalid range inside inside " + sectionName);
                Bukkit.getLogger().info("The range must be in the format of 'start-end' or 'number'");
            }
        }
    }

    public String getKey() {
        return key;
    }

    /**
     * @return the slots
     */
    @Override
    public Set<Integer> getSlots() {
        return (Set<Integer>) super.getSlots();
    }
}
