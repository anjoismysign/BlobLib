package us.mytheria.bloblib.entities.inventory;

import org.bukkit.entity.EntityType;
import us.mytheria.bloblib.BlobLib;

import java.util.UUID;

public class SpawnableEntitySelector extends VariableSelector<EntityType> {

    public static SpawnableEntitySelector build(UUID builderId) {
        SharableInventory inventory = VariableSelector.DEFAULT();
        return new SpawnableEntitySelector(inventory, builderId);
    }

    private SpawnableEntitySelector(SharableInventory blobInventory, UUID builderId) {
        super(blobInventory, builderId, "ENTITYTYPE",
                BlobLib.getInstance().getFillerManager().getSpawnableEntityTypeFiller());
    }
}
