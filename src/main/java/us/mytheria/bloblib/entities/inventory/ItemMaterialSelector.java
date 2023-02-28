package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Material;
import us.mytheria.bloblib.BlobLib;

import java.util.UUID;

public class ItemMaterialSelector extends VariableSelector<Material> {

    public static ItemMaterialSelector build(UUID builderId) {
        SharableInventory inventory = VariableSelector.DEFAULT();
        return new ItemMaterialSelector(inventory, builderId);
    }

    private ItemMaterialSelector(SharableInventory blobInventory, UUID builderId) {
        super(blobInventory, builderId, "MATERIAL",
                BlobLib.getInstance().getFillerManager().getItemMaterialFiller());
    }
}
