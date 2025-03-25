package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.action.ActionMemo;
import us.mytheria.bloblib.action.ActionType;
import us.mytheria.bloblib.api.BlobLibActionAPI;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;
import us.mytheria.bloblib.entities.translatable.TranslatableItem;
import us.mytheria.bloblib.exception.ConfigurationFieldException;
import us.mytheria.bloblib.itemstack.ItemStackReader;
import us.mytheria.bloblib.utilities.IntegerRange;

import java.util.ArrayList;
import java.util.List;
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
    @NotNull
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
        ItemStack itemStack;
        if (section.isString("ItemStack")) {
            String reference = section.getString("ItemStack");
            TranslatableItem translatableItem = BlobLibTranslatableAPI.getInstance()
                    .getTranslatableItem(reference,
                            locale);
            if (translatableItem == null)
                throw new ConfigurationFieldException("TranslatableItem not found: " + reference);
            itemStack = translatableItem.getClone();
        } else {
            ConfigurationSection itemStackSection = section.getConfigurationSection("ItemStack");
            if (itemStackSection == null)
                throw new ConfigurationFieldException("'ItemStack' ConfigurationSection is null");
            itemStack = ItemStackReader.READ_OR_FAIL_FAST(itemStackSection).build();
        }
        if (section.isInt("Amount")) {
            int amount = section.getInt("Amount");
            itemStack.setAmount(amount);
        }
        String read = section.getString("Slot", "-1");
        Set<Integer> set = IntegerRange.getInstance().parse(read);
        String hasPermission = null;
        boolean isPermissionInverted = false;
        if (section.isString("Permission"))
            hasPermission = section.getString("Permission");
        if (section.isString("Permission-Denied")) {
            isPermissionInverted = true;
            hasPermission = section.getString("Permission-Denied");
        }
        double hasMoney = 0;
        boolean isMoneyInverted = false;
        if (section.isDouble("Price")) {
            hasMoney = section.getDouble("Price");
        }
        if (section.isDouble("Price-Denied")) {
            isMoneyInverted = true;
            hasMoney = section.getDouble("Price-Denied");
        }
        String hasTranslatableItem = null;
        boolean isTranslatableItemInverted = false;
        if (section.isString("Has-Translatable-Item"))
            hasTranslatableItem = section.getString("Has-Translatable-Item");
        if (section.isString("Has-Translatable-Item-Denied")) {
            isTranslatableItemInverted = true;
            hasTranslatableItem = section.getString("Has-Translatable-Item-Denied");
        }

        String priceCurrency = null;
        if (section.isString("Price-Currency"))
            priceCurrency = section.getString("Price-Currency");
        String action = null;
        if (section.isString("Action"))
            action = section.getString("Action");
        if (action != null && BlobLibActionAPI.getInstance().getAction(action) == null)
            throw new ConfigurationFieldException("'Action' doesn't point to a valid Action: " + action);
        ActionType actionType = null;
        ConfigurationSection singleActionSection = section.getConfigurationSection("Action");
        if (singleActionSection != null) {
            if (!singleActionSection.isString("Action"))
                Bukkit.getLogger().info("'Action' field is missing in 'Action' ConfigurationSection (" + key + ".Action.Action)");
            if (!singleActionSection.isString("Action-Type"))
                Bukkit.getLogger().info("'Action-Type' field is missing in 'Action' ConfigurationSection (" + key + ".Action.Action-Type)");
            action = singleActionSection.getString("Action");
            try {
                actionType = ActionType.valueOf(singleActionSection.getString("Action-Type"));
            } catch ( IllegalArgumentException exception ) {
                throw new ConfigurationFieldException("Invalid 'ActionType' for " + key + ".Action.Action-Type");
            }
        }
        List<ActionMemo> actions = new ArrayList<>();
        if (section.isConfigurationSection("Actions")) {
            ConfigurationSection actionsSection = section.getConfigurationSection("Actions");
            for (String key1 : actionsSection.getKeys(false)) {
                ConfigurationSection actionSection = actionsSection.getConfigurationSection(key1);
                String reference;
                ActionType type = null;
                if (actionSection != null) {
                    String path = key + ".Actions." + key1;
                    if (!actionSection.isString("Action"))
                        Bukkit.getLogger().info("'Action' field is missing in 'Action' ConfigurationSection (" + path + ")");
                    if (!actionSection.isString("Action-Type"))
                        Bukkit.getLogger().info("'Action-Type' field is missing in 'Action' ConfigurationSection (" + path + ")");
                    reference = actionSection.getString("Action");
                    try {
                        type = ActionType.valueOf(actionSection.getString("Action-Type"));
                    } catch ( IllegalArgumentException exception ) {
                        throw new ConfigurationFieldException("Invalid 'ActionType' for " + key + ".Action.Action-Type");
                    }
                    actions.add(new ActionMemo(reference, type));
                } else if (actionsSection.isString(key1)) {
                    reference = actionsSection.getString(key1);
                    actions.add(new ActionMemo(reference, null));
                }
            }
        }
        if (action != null)
            actions.add(new ActionMemo(action, actionType));
        return new BlobMultiSlotable(set, itemStack, key, hasPermission, hasMoney,
                priceCurrency, actions, hasTranslatableItem, isPermissionInverted,
                isMoneyInverted, isTranslatableItemInverted);
    }

    /**
     * Constructor for BlobMultiSlotable
     *
     * @param slots                      The slots to add the item to
     * @param itemStack                  The item to add
     * @param key                        The key to use for the item
     * @param hasPermission              If the player needs to have a permission.
     * @param hasMoney                   If the player needs to have money.
     * @param priceCurrency              The price currency to use for the item
     * @param actions                    The actions to use for the item
     * @param hasTranslatableItem        If the player needs to have a TranslatableItem.
     * @param isPermissionInverted       If the permission check is inverted.
     * @param isMoneyInverted            If the money check is inverted.
     * @param isTranslatableItemInverted If the TranslatableItem check is inverted.
     */
    public BlobMultiSlotable(@NotNull Set<Integer> slots,
                             @NotNull ItemStack itemStack,
                             @NotNull String key,
                             @Nullable String hasPermission,
                             double hasMoney,
                             @Nullable String priceCurrency,
                             @NotNull List<ActionMemo> actions,
                             @Nullable String hasTranslatableItem,
                             boolean isPermissionInverted,
                             boolean isMoneyInverted,
                             boolean isTranslatableItemInverted) {
        super(slots, itemStack, hasPermission, hasMoney, priceCurrency, actions,
                hasTranslatableItem, isPermissionInverted, isMoneyInverted, isTranslatableItemInverted);
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
        return new InventoryButton(key, getSlots(), getHasPermission(), getHasMoney(),
                getMoneyCurrency(), getActions(), getHasTranslatableItem(),
                isPermissionInverted(), isMoneyInverted(), isTranslatableItemInverted());
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
                } catch ( NumberFormatException exception ) {
                    Bukkit.getLogger().info(sectionName + " got a slot that's not a number");
                    Bukkit.getLogger().info("This is not possible in an inventory so it was set");
                    Bukkit.getLogger().info("to '0' which is default.");
                }
                int end = 0;
                try {
                    end = Integer.parseInt(split[1]);
                } catch ( NumberFormatException exception ) {
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
