package us.mytheria.bloblib.entities;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an object used in shard/multiple-server instances and can be
 * owned by multiple players/owners.
 */
public interface SharedSerializable extends BinarySerializable {
    /**
     * Will get the owners of this object.
     * If not set before, it will return an empty list.
     *
     * @return The owners of this object.
     */
    @NotNull
    default List<String> getOwners() {
        return blobCrudable().hasStringList("SharedSerializable#Owners")
                .orElse(new ArrayList<>());
    }

    /**
     * Will set the owners of this object.
     *
     * @param owners The owners of this object.
     */
    default void setOwners(List<String> owners) {
        blobCrudable().getDocument().put("SharedSerializable#Owners", owners);
    }

    /**
     * Will add an owner to this object.
     * If already an owner, it will do nothing (no duplicates).
     *
     * @param owner The owner to add.
     */
    default void addOwner(String owner) {
        List<String> owners = getOwners();
        if (owners.contains(owner))
            return;
        owners.add(owner);
        setOwners(owners);
    }

    /**
     * Will remove an owner from this object.
     * If not an owner, no issues will occur.
     *
     * @param owner The owner to remove.
     */
    default void removeOwner(String owner) {
        List<String> owners = getOwners();
        owners.remove(owner);
        setOwners(owners);
    }
}
