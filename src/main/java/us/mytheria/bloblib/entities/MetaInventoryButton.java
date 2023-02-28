package us.mytheria.bloblib.entities;

import us.mytheria.bloblib.entities.inventory.InventoryButton;

import java.util.Set;

public class MetaInventoryButton extends InventoryButton {
    private final String meta;
    private final String subMeta;

    public static MetaInventoryButton fromInventoryButton(InventoryButton button, String meta,
                                                          String subMeta) {
        return new MetaInventoryButton(button.getKey(), button.getSlots(), meta,
                subMeta);
    }

    public MetaInventoryButton(String key, Set<Integer> slots, String meta,
                               String subMeta) {
        super(key, slots);
        this.meta = meta;
        this.subMeta = subMeta;
    }

    public String getMeta() {
        return meta;
    }

    public String getSubMeta() {
        return subMeta;
    }
}
