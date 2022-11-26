package us.mytheria.bloblib.entities.inventory;

import org.bukkit.entity.EntityType;
import us.mytheria.bloblib.BlobLib;

import java.util.UUID;

public class SpawnableEntitySelector extends VariableSelector {

    public static SpawnableEntitySelector build(UUID builderId) {
        BlobInventory inventory = VariableSelector.DEFAULT();
        return new SpawnableEntitySelector(inventory, builderId);
    }

    private SpawnableEntitySelector(BlobInventory blobInventory, UUID builderId) {
        super(blobInventory, builderId, "ENTITYTYPE",
                BlobLib.getInstance().getFillerManager().getSpawnableEntityTypeFiller());
    }

    @Override
    public EntityType getValue(int slot) {
        return (EntityType) getValues().get(slot);
    }
}
