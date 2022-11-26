package us.mytheria.bloblib.entities.inventory;

import org.bukkit.entity.EntityType;
import us.mytheria.bloblib.BlobLib;

import java.util.UUID;

public class SpawnableEntitySelector extends VariableSelector {

    public static VariableSelector build(UUID builderId) {
        BlobInventory inventory = VariableSelector.DEFAULT();
        return new SpawnableEntitySelector(inventory, builderId);
    }

    public SpawnableEntitySelector(BlobInventory blobInventory, UUID builderId) {
        super(blobInventory, builderId, "ENTITYTYPE",
                BlobLib.getInstance().getFillerManager().getSpawnableEntityTypeFiller());
    }

    @Override
    public EntityType getValue(int slot) {
        return (EntityType) getValues().get(slot);
    }
}
