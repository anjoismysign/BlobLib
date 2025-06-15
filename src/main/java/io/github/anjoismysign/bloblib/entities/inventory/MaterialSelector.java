package io.github.anjoismysign.bloblib.entities.inventory;

import io.github.anjoismysign.bloblib.BlobLib;
import org.bukkit.Material;

import java.util.UUID;

public class MaterialSelector extends VariableSelector<Material> {

    public static MaterialSelector build(UUID builderId) {
        BlobInventory inventory = VariableSelector.DEFAULT();
        return new MaterialSelector(inventory, builderId);
    }

    private MaterialSelector(BlobInventory blobInventory, UUID builderId) {
        super(blobInventory, builderId, "MATERIAL",
                BlobLib.getInstance().getFillerManager().getMaterialFiller(),
                null);
    }
}
