package io.github.anjoismysign.bloblib.entities.inventory;

import io.github.anjoismysign.bloblib.BlobLib;
import org.bukkit.Material;

import java.util.UUID;

public class BlockMaterialSelector extends VariableSelector<Material> {

    public static BlockMaterialSelector build(UUID builderId) {
        BlobInventory inventory = VariableSelector.DEFAULT();
        return new BlockMaterialSelector(inventory, builderId);
    }

    private BlockMaterialSelector(BlobInventory blobInventory, UUID builderId) {
        super(blobInventory, builderId, "BLOCKMATERIAL",
                BlobLib.getInstance().getFillerManager().getBlockMaterialFiller(),
                null);
    }
}
