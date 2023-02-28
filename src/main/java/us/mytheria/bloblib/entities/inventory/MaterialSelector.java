package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Material;
import us.mytheria.bloblib.BlobLib;

import java.util.UUID;

public class MaterialSelector extends VariableSelector<Material> {

    public static MaterialSelector build(UUID builderId) {
        SharableInventory inventory = VariableSelector.DEFAULT();
        return new MaterialSelector(inventory, builderId);
    }

    private MaterialSelector(SharableInventory blobInventory, UUID builderId) {
        super(blobInventory, builderId, "MATERIAL",
                BlobLib.getInstance().getFillerManager().getMaterialFiller());
    }
}
