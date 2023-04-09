package us.mytheria.bloblib.entities.inventory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class MetaInventoryButton extends InventoryButton {
    private final String meta;
    private final String subMeta;

    public static MetaInventoryButton fromInventoryButton(InventoryButton button, String meta,
                                                          String subMeta) {
        return new MetaInventoryButton(button.getKey(), button.getSlots(), meta,
                subMeta, button.getPermission(), button.getPrice(),
                button.getPriceCurrency(), button.getAction());
    }

    public MetaInventoryButton(String key, Set<Integer> slots, String meta,
                               @Nullable String subMeta, @Nullable String permission,
                               double price, @Nullable String priceCurrency,
                               @Nullable String action) {
        super(key, slots, permission, price, priceCurrency, action);
        this.meta = meta;
        this.subMeta = subMeta;
    }

    /**
     * @return Whether the MetaInventoryButton has meta or not
     */
    public boolean hasMeta() {
        return !getMeta().equals("NONE");
    }

    /**
     * @return The meta of the MetaInventoryButton
     */
    @NotNull
    public String getMeta() {
        return meta;
    }

    /**
     * @return The subMeta of the MetaInventoryButton if it has one
     * null otherwise
     */
    @Nullable
    public String getSubMeta() {
        return subMeta;
    }
}
