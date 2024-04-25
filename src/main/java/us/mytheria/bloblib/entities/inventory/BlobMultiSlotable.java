package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.action.ActionType;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;
import us.mytheria.bloblib.entities.translatable.TranslatableItem;
import us.mytheria.bloblib.itemstack.ItemStackReader;
import us.mytheria.bloblib.utilities.IntegerRange;

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
public class BlobMultiSlotable extends MultiSlotable {
    private final String key;

    /**
     * Parses/reads from a ConfigurationSection using ItemStackReader.
     *
     * @param section The ConfigurationSection to read from.
     * @param key     The key of the BlobMultiSlotable which was intended to read from.
     * @return The BlobMultiSlotable which was read from the ConfigurationSection.
     */
    public static BlobMultiSlotable read(ConfigurationSection section, String key,
                                         String locale) {
        if (section.isString("ItemStack")) {
            String reference = section.getString("ItemStack");
            TranslatableItem translatableItem = BlobLibTranslatableAPI.getInstance()
                    .getTranslatableItem(reference,
                            locale);
            if (translatableItem == null)
                throw new NullPointerException("TranslatableItem not found: " + reference);
            String read = section.getString("Slot", "-1");
            Set<Integer> list = IntegerRange.getInstance().parse(read);
            String permission = null;
            if (section.isString("Permission"))
                permission = section.getString("Permission");
            double price = 0;
            if (section.isDouble("Price")) {
                price = section.getDouble("Price");
            }
            String priceCurrency = null;
            if (section.isString("Price-Currency"))
                priceCurrency = section.getString("Price-Currency");
            String action = null;
            if (section.isString("Action"))
                action = section.getString("Action");
            ActionType actionType = null;
            ConfigurationSection actionSection = section.getConfigurationSection("Action");
            if (actionSection != null) {
                if (!actionSection.isString("Action"))
                    Bukkit.getLogger().info("'Action' field is missing in 'Action' ConfigurationSection (" + key + ".Action.Action)");
                if (!actionSection.isString("Action-Type"))
                    Bukkit.getLogger().info("'Action-Type' field is missing in 'Action' ConfigurationSection (" + key + ".Action.Action-Type)");
                action = actionSection.getString("Action");
                try {
                    actionType = ActionType.valueOf(actionSection.getString("Action-Type"));
                } catch (IllegalArgumentException e) {
                    BlobLib.getAnjoLogger().singleError("Invalid 'ActionType' for " + key + ".Action.Action-Type");
                }
            }
            ItemStack clone = translatableItem.getClone();
            int amount = section.getInt("Amount", 1);
            clone.setAmount(amount);
            return new BlobMultiSlotable(list, clone, key, permission, price,
                    priceCurrency, action, actionType);
        }
        ConfigurationSection itemStackSection = section.getConfigurationSection("ItemStack");
        if (itemStackSection == null) {
            Bukkit.getLogger().severe("ItemStack section is null for " + key);
            return null;
        }
        ItemStack itemStack = ItemStackReader.getInstance().readOrFailFast(itemStackSection);
        String read = section.getString("Slot", "-1");
        Set<Integer> list = IntegerRange.getInstance().parse(read);
        String permission = null;
        if (section.isString("Permission"))
            permission = section.getString("Permission");
        double price = 0;
        if (section.isDouble("Price")) {
            price = section.getDouble("Price");
        }
        String priceCurrency = null;
        if (section.isString("Price-Currency"))
            priceCurrency = section.getString("Price-Currency");
        String action = null;
        if (section.isString("Action"))
            action = section.getString("Action");
        ActionType actionType = null;
        ConfigurationSection actionSection = section.getConfigurationSection("Action");
        if (actionSection != null) {
            if (!actionSection.isString("Action"))
                Bukkit.getLogger().info("'Action' field is missing in 'Action' ConfigurationSection (" + key + ".Action.Action)");
            if (!actionSection.isString("Action-Type"))
                Bukkit.getLogger().info("'Action-Type' field is missing in 'Action' ConfigurationSection (" + key + ".Action.Action-Type)");
            action = actionSection.getString("Action");
            try {
                actionType = ActionType.valueOf(actionSection.getString("Action-Type"));
            } catch (IllegalArgumentException e) {
                BlobLib.getAnjoLogger().singleError("Invalid 'ActionType' for " + key + ".Action.Action-Type");
            }
        }
        return new BlobMultiSlotable(list, itemStack, key, permission, price,
                priceCurrency, action, actionType);
    }

    /**
     * Constructor for BlobMultiSlotable
     *
     * @param slots         The slots to add the item to
     * @param itemStack     The item to add
     * @param key           The key to use for the item
     * @param permission    The permission to use for the item
     * @param price         The price to use for the item
     * @param priceCurrency The price currency to use for the item
     * @param action        The action to use for the item
     */
    public BlobMultiSlotable(Set<Integer> slots, ItemStack itemStack,
                             String key,
                             @Nullable String permission,
                             double price,
                             @Nullable String priceCurrency,
                             @Nullable String action,
                             @Nullable ActionType actionType) {
        super(slots, itemStack, permission, price, priceCurrency, action, actionType);
        this.key = key;
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

    public InventoryButton toInventoryButton() {
        return new InventoryButton(key, getSlots(), getPermission(), getPrice(),
                getPriceCurrency(), getAction(), getActionType());
    }

    /**
     * Writes in a ButtonManager (in case of working
     * with BlobLib's GUI system) the BlobMultiSlotable
     *
     * @param buttonManager The ButtonManager to write in
     *                      the BlobMultiSlotable
     */
    public void setInButtonManager(ButtonManager<InventoryButton> buttonManager) {
        buttonManager.getStringKeys().put(key, toInventoryButton());
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
     * @return the slots that the ItemStack belongs to
     */
    @Override
    public Set<Integer> getSlots() {
        return (Set<Integer>) super.getSlots();
    }
}
