package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.itemstack.ItemStackReader;

import java.util.HashSet;
import java.util.Set;

/**
 * @author anjoismysign
 * <p>
 * BlobMultiSlotable is an instance of MultiSlotable which
 * itself contains an extra attribute which would be
 * a String 'key'. BlobMultiSlotable contains parsing
 * methods from ConfigurationSection's and methods
 * related to insertion into org.bukkit.inventory.Inventory
 * or even us.mytheria.bloblib.entities.inventory.ButtonManager
 * in case of working with BlobLib's GUI system.
 * The 'key' attribute is used to identify the BlobMultiSlotable
 * in case of failure to be able to detail/trace the error.
 */
public class MetaBlobMultiSlotable extends MultiSlotable {
    private final String key;
    private final String meta;
    private final String subMeta;

    /**
     * Parses/reads from a ConfigurationSection using ItemStackReader.
     *
     * @param section The ConfigurationSection to read from.
     * @param key     The key of the BlobMultiSlotable which was intended to read from.
     * @return The BlobMultiSlotable which was read from the ConfigurationSection.
     */
    public static MetaBlobMultiSlotable read(ConfigurationSection section, String key) {
        ConfigurationSection itemStackSection = section.getConfigurationSection("ItemStack");
        if (itemStackSection == null) {
            Bukkit.getLogger().severe("ItemStack section is null for " + key);
            return null;
        }
        ItemStack itemStack = ItemStackReader.read(itemStackSection).build();
        HashSet<Integer> set = new HashSet<>();
        String read = section.getString("Slot", "-1");
        String[] slots = read.split(",");
        if (slots.length != 1) {
            for (String slot : slots) {
                add(set, slot, section.getName());
            }
        } else {
            add(set, read, section.getName());
        }
        String meta = section.getString("Meta", "NONE");
        String subMeta = null;
        if (section.isString("SubMeta")) {
            subMeta = section.getString("SubMeta");
        }
        String permission = null;
        if (section.isString("Permission")) {
            permission = section.getString("Permission");
        }
        double price = 0;
        if (section.isDouble("Price")) {
            price = section.getDouble("Price");
        }
        String priceCurrency = null;
        if (section.isString("Price-Currency")) {
            priceCurrency = section.getString("Price-Currency");
        }
        return new MetaBlobMultiSlotable(set, itemStack, key, meta, subMeta,
                permission, price, priceCurrency);
    }

    /**
     * Constructor for BlobMultiSlotable
     *
     * @param slots     The slots to add the item to
     * @param itemStack The item to add
     * @param key       The key to use for the item
     */
    public MetaBlobMultiSlotable(Set<Integer> slots, ItemStack itemStack, String key,
                                 String meta, @Nullable String subMeta,
                                 @Nullable String permission,
                                 double price,
                                 @Nullable String priceCurrency) {
        super(slots, itemStack, permission, price, priceCurrency);
        this.key = key;
        this.meta = meta;
        this.subMeta = subMeta;
    }

    /**
     * Will insert the BlobMultiSlotable into the given Inventory.
     * The ItemStack is not cloned, so they all should be references
     * in case of retrieving in the future.
     *
     * @param inventory The inventory to insert the ItemStacks
     */
    public void setInInventory(Inventory inventory) {
        for (Integer slot : getSlots()) {
            inventory.setItem(slot, getItemStack());
        }
    }

    public MetaInventoryButton toMetaInventoryButton() {
        return new MetaInventoryButton(key, getSlots(), meta, subMeta,
                getPermission(), getPrice(), getPriceCurrency());
    }

    /**
     * Writes in a ButtonManager (in case of working
     * with BlobLib's GUI system) the BlobMultiSlotable
     *
     * @param buttonManager The ButtonManager to write in
     *                      the BlobMultiSlotable
     */
    public void setInButtonManager(ButtonManager<MetaInventoryButton> buttonManager) {
        buttonManager.getStringKeys().put(key, toMetaInventoryButton());
        for (Integer slot : getSlots()) {
            buttonManager.getIntegerKeys().put(slot, getItemStack());
        }
    }

    /**
     * Adds slots to an existing set. Used in parsing
     * from ConfigurationSection.
     *
     * @param set         the set to add
     * @param raw         the raw string to parse. should be a range or a single number, i.e. 1-7 or 8
     * @param sectionName the name of the section, used for logging/debugging
     */
    private static void add(Set<Integer> set, String raw, String sectionName) {
        String[] split = raw.split("-");
        switch (split.length) {
            /*
            if String.split(regex) has no match will return String itself
            which is a length of "1".
            Example for this case would be if 'raw' is "4" instead of "1-7"
             */
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

    /**
     * @return The key/identifier of the BlobMultiSlotable
     */
    public String getKey() {
        return key;
    }

    /**
     * @return Whether the BlobMultiSlotable has meta or not
     */
    public boolean hasMeta() {
        return !meta.equals("NONE");
    }

    /**
     * @return The meta of the BlobMultiSlotable
     */
    public String getMeta() {
        return meta;
    }

    /**
     * @return The subMeta of the BlobMultiSlotable
     */
    @Nullable
    public String getSubMeta() {
        return subMeta;
    }

    /**
     * @return the slots that the ItemStack belongs to
     */
    @Override
    public Set<Integer> getSlots() {
        return (Set<Integer>) super.getSlots();
    }
}
