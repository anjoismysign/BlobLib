package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Material;
import us.mytheria.bloblib.BlobLib;

import java.util.UUID;

public class BlockMaterialSelector extends VariableSelector {

    public static BlockMaterialSelector build(UUID builderId) {
        BlobInventory inventory = VariableSelector.DEFAULT();
        return new BlockMaterialSelector(inventory, builderId);
    }

    private BlockMaterialSelector(BlobInventory blobInventory, UUID builderId) {
        super(blobInventory, builderId, "BLOCKMATERIAL",
                BlobLib.getInstance().getFillerManager().getBlockMaterialFiller());
    }

    @Override
    public Material getValue(int slot) {
        return (Material) getValues().get(slot);
    }
}
