package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Material;
import us.mytheria.bloblib.BlobLib;

import java.util.UUID;

public class MaterialSelector extends VariableSelector {

    public static VariableSelector build(UUID builderId) {
        BlobInventory inventory = VariableSelector.DEFAULT();
        return new MaterialSelector(inventory, builderId);
    }

    public MaterialSelector(BlobInventory blobInventory, UUID builderId) {
        super(blobInventory, builderId, "MATERIAL",
                BlobLib.getInstance().getFillerManager().getMaterialFiller());
    }

    @Override
    public Material getValue(int slot) {
        return (Material) getValues().get(slot);
    }
}
