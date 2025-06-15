package io.github.anjoismysign.bloblib.entities.inventory;

import io.github.anjoismysign.bloblib.BlobLib;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class SpawnableEntitySelector extends VariableSelector<EntityType> {

    public static SpawnableEntitySelector build(UUID builderId) {
        BlobInventory inventory = VariableSelector.DEFAULT();
        return new SpawnableEntitySelector(inventory, builderId);
    }

    private SpawnableEntitySelector(BlobInventory blobInventory, UUID builderId) {
        super(blobInventory, builderId, "ENTITYTYPE",
                BlobLib.getInstance().getFillerManager().getSpawnableEntityTypeFiller(),
                null);
    }
}
