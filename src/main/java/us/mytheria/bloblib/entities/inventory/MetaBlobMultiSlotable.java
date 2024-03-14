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
    public static MetaBlobMultiSlotable read(ConfigurationSection section, String key,
                                             String locale) {
        if (section.isString("ItemStack")) {
            String reference = section.getString("ItemStack");
            TranslatableItem translatableItem = BlobLibTranslatableAPI.getInstance()
                    .getTranslatableItem(reference,
                            locale);
            if (translatableItem == null)
                throw new NullPointerException("TranslatableItem not found: " + reference);
            String read = section.getString("Slot", "-1");
            Set<Integer> set = IntegerRange.getInstance().parse(read);
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
            return new MetaBlobMultiSlotable(set, clone, key, meta, subMeta,
                    permission, price, priceCurrency, action, actionType);
        }
        ConfigurationSection itemStackSection = section.getConfigurationSection("ItemStack");
        if (itemStackSection == null) {
            Bukkit.getLogger().severe("ItemStack section is null for " + key);
            return null;
        }
        ItemStack itemStack = ItemStackReader.READ_OR_FAIL_FAST(itemStackSection).build();
        String read = section.getString("Slot", "-1");
        Set<Integer> set = IntegerRange.getInstance().parse(read);
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
        return new MetaBlobMultiSlotable(set, itemStack, key, meta, subMeta,
                permission, price, priceCurrency, action, actionType);
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
                                 @Nullable String priceCurrency,
                                 @Nullable String action,
                                 @Nullable ActionType actionType) {
        super(slots, itemStack, permission, price, priceCurrency, action, actionType);
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
                getPermission(), getPrice(), getPriceCurrency(),
                getAction(), getActionType());
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
