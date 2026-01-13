package io.github.anjoismysign.bloblib.entities.inventory;

import io.github.anjoismysign.bloblib.action.ActionMemo;
import io.github.anjoismysign.bloblib.action.ActionType;
import io.github.anjoismysign.bloblib.api.BlobLibActionAPI;
import io.github.anjoismysign.bloblib.api.BlobLibTranslatableAPI;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableItem;
import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import io.github.anjoismysign.bloblib.middleman.itemstack.ItemStackReader;
import io.github.anjoismysign.bloblib.utilities.IntegerRange;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author anjoismysign
 * <p>
 * MetaBlobMultiSlotable is an instance of MultiSlotable which
 * itself contains an extra attribute which would be
 * a String 'key'. BlobMultiSlotable contains parsing
 * methods from ConfigurationSection's and methods
 * related to insertion into org.bukkit.inventory.Inventory
 * or even io.github.anjoismysign.bloblib.entities.inventory.ButtonManager
 * in case of working with BlobLib's GUI system.
 * The 'key' attribute is used to identify the BlobMultiSlotable
 * in case of failure to be able to detail/trace the error.
 */
public class MetaBlobMultiSlotable extends MultiSlotable {
    @NotNull
    private final String key;
    @NotNull
    private final String meta;
    @Nullable
    private final String subMeta;

    /**
     * Parses/reads from a ConfigurationSection using ItemStackReader.
     *
     * @param section The ConfigurationSection to read from.
     * @param identifier The identifier of the BlobMultiSlotable which was intended to read from.
     * @return The BlobMultiSlotable which was read from the ConfigurationSection.
     */
    public static MetaBlobMultiSlotable read(ConfigurationSection section,
                                             String identifier,
                                             String locale) {
        final Supplier<ItemStack> readSupplier;
        if (section.isString("ItemStack")) {
            String reference = section.getString("ItemStack");
            TranslatableItem translatableItem = BlobLibTranslatableAPI.getInstance()
                    .getTranslatableItem(reference,
                            locale);
            if (translatableItem == null)
                throw new ConfigurationFieldException("TranslatableItem not found: " + reference);
            readSupplier = () -> translatableItem.getClone();
        } else {
            ConfigurationSection itemStackSection = section.getConfigurationSection("ItemStack");
            if (itemStackSection == null)
                throw new ConfigurationFieldException("'ItemStack' ConfigurationSection is null");
            readSupplier = () -> ItemStackReader.OMNI_STACK(itemStackSection, null).getCopy();
        }
        final Supplier<ItemStack> supplier;
        if (section.isInt("Amount")) {
            int amount = section.getInt("Amount");
            supplier = () -> {
                ItemStack itemStack = readSupplier.get();
                itemStack.setAmount(amount);
                return itemStack;
            };
        } else {
            supplier = readSupplier;
        }
        String read = section.getString("Slot", "-1");
        Set<Integer> set = IntegerRange.getInstance().parse(read);
        String meta = section.getString("Meta", "NONE");
        String subMeta = null;
        if (section.isString("SubMeta")) {
            subMeta = section.getString("SubMeta");
        }
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
                Bukkit.getLogger().info("'Action' field is missing in 'Action' ConfigurationSection (" + identifier + ".Action.Action)");
            if (!singleActionSection.isString("Action-Type"))
                Bukkit.getLogger().info("'Action-Type' field is missing in 'Action' ConfigurationSection (" + identifier + ".Action.Action-Type)");
            action = singleActionSection.getString("Action");
            try {
                actionType = ActionType.valueOf(singleActionSection.getString("Action-Type"));
            } catch (IllegalArgumentException exception) {
                throw new ConfigurationFieldException("Invalid 'ActionType' for " + identifier + ".Action.Action-Type");
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
                    String path = identifier + ".Actions." + key1;
                    if (!actionSection.isString("Action"))
                        Bukkit.getLogger().info("'Action' field is missing in 'Action' ConfigurationSection (" + path + ")");
                    if (!actionSection.isString("Action-Type"))
                        Bukkit.getLogger().info("'Action-Type' field is missing in 'Action' ConfigurationSection (" + path + ")");
                    reference = actionSection.getString("Action");
                    try {
                        type = ActionType.valueOf(actionSection.getString("Action-Type"));
                    } catch (IllegalArgumentException exception) {
                        throw new ConfigurationFieldException("Invalid 'ActionType' for " + identifier + ".Action.Action-Type");
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
        return new MetaBlobMultiSlotable(set, supplier, identifier, meta, subMeta,
                hasPermission, hasMoney, priceCurrency, actions,
                hasTranslatableItem, isPermissionInverted, isMoneyInverted,
                isTranslatableItemInverted);
    }

    /**
     * Constructor for MetaBlobMultiSlotable
     *
     * @param slots                      The slots to add the item to
     * @param supplier                   The ItemStack supplier to add
     * @param meta                       The meta to use for the item
     * @param subMeta                    The subMeta to use for the item
     * @param key                        The key to use for the item
     * @param hasPermission              If the player needs to have a permission.
     * @param hasMoney                   If the player needs to have money.
     * @param priceCurrency              The price currency to use for the item
     * @param actions                    The actions to use for the item
     * @param hasTranslatableItem        If the player needs to have an item display.
     * @param isPermissionInverted       If the permission check is inverted.
     * @param isMoneyInverted            If the money check is inverted.
     * @param isTranslatableItemInverted If the TranslatableItem check is inverted.
     */
    public MetaBlobMultiSlotable(@NotNull Set<Integer> slots,
                                 @NotNull Supplier<ItemStack> supplier,
                                 @NotNull String key,
                                 @NotNull String meta,
                                 @Nullable String subMeta,
                                 @Nullable String hasPermission,
                                 double hasMoney,
                                 @Nullable String priceCurrency,
                                 @NotNull List<ActionMemo> actions,
                                 @Nullable String hasTranslatableItem,
                                 boolean isPermissionInverted,
                                 boolean isMoneyInverted,
                                 boolean isTranslatableItemInverted) {
        super(slots, supplier, hasPermission, hasMoney, priceCurrency, actions,
                hasTranslatableItem, isPermissionInverted, isMoneyInverted,
                isTranslatableItemInverted);
        this.key = key;
        this.meta = meta;
        this.subMeta = subMeta;
    }

    public MetaInventoryButton toMetaInventoryButton() {
        return new MetaInventoryButton(key, getSlots(), meta, subMeta,
                getHasPermission(), getHasMoney(), getMoneyCurrency(),
                getActions(), getHasTranslatableItem(), isPermissionInverted(),
                isMoneyInverted(), isTranslatableItemInverted());
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
