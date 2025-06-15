package io.github.anjoismysign.bloblib.entities.inventory;

import io.github.anjoismysign.bloblib.BlobLib;
import org.bukkit.Material;

import java.util.UUID;

public class ItemMaterialSelector extends VariableSelector<Material> {

    public static ItemMaterialSelector build(UUID builderId) {
        BlobInventory inventory = VariableSelector.DEFAULT();
        return new ItemMaterialSelector(inventory, builderId);
    }

    private ItemMaterialSelector(BlobInventory blobInventory, UUID builderId) {
        super(blobInventory, builderId, "MATERIAL",
                BlobLib.getInstance().getFillerManager().getItemMaterialFiller(),
                null);
    }
}
