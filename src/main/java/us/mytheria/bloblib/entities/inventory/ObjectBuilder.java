package us.mytheria.bloblib.entities.inventory;

import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.entities.BlobObject;
import us.mytheria.bloblib.entities.ObjectDirector;

import java.util.UUID;

/**
 * @param <T> the type of object to build
 * @author anjoismysign
 * <p>
 * An ObjectBuilder is an inventory that allows the user to build an object.
 * The idea behind it is that through the Minecraft client, while connected
 * to the Minecraft server, the user can build an object by interacting with
 * a Bukkit Inventory / GUI.
 */
public abstract class ObjectBuilder<T extends BlobObject> extends BlobObjectBuilder<T> {
    private final ObjectDirector<T> objectDirector;

    /**
     * Constructs a new ObjectBuilder.
     *
     * @param blobInventory  the inventory to use
     * @param builderId      the builder's UUID
     * @param objectDirector the ObjectDirector to which this ObjectBuilder belongs to
     */
    public ObjectBuilder(@NotNull BlobInventory blobInventory,
                         @NotNull UUID builderId,
                         @NotNull ObjectDirector<T> objectDirector) {
        super(blobInventory, builderId);
        this.objectDirector = objectDirector;
    }

    /**
     * Retrieves the ObjectDirector to which
     * this ObjectBuilder belongs to.
     *
     * @return the ObjectDirector
     */
    public ObjectDirector<T> getObjectDirector() {
        return objectDirector;
    }
}
