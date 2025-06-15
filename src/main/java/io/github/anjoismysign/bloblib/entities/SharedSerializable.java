package io.github.anjoismysign.bloblib.entities;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents an object used in shard/multiple-server instances and can be
 * owned by multiple players/owners.
 */
public interface SharedSerializable<T extends SerializableProfile> extends BinarySerializable {
    /**
     * Gets all proprietors in fast-access memory
     *
     * @return All proprietors in memory
     */
    Map<String, T> getProprietors();

    /**
     * Adds a proprietor to fast-access memory.
     *
     * @param proprietor The proprietor to add.
     */
    default void addProprietor(T proprietor) {
        getProprietors().put(proprietor.getIdentification(), proprietor);
    }

    /**
     * Removes a proprietor from fast-access memory.
     *
     * @param identification The identification of the proprietor to remove.
     * @return The proprietor that was removed.
     */
    default T removeProprietor(String identification) {
        return getProprietors().remove(identification);
    }

    /**
     * Removes a proprietor from fast-access memory.
     *
     * @param proprietor The proprietor to remove.
     * @return The proprietor that was removed.
     */
    default T removeProprietor(T proprietor) {
        return removeProprietor(proprietor.getIdentification());
    }

    /**
     * Will unpack the owners from the document.
     * If the document does not contain any owners, it will return an empty list.
     *
     * @return The owners of this object.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    default List<Map<String, Object>> unpackProprietors() {
        List<Map<String, Object>> serializedProprietors = blobCrudable().getDocument().get("SharedSerializable#Proprietors", List.class);
        return serializedProprietors == null ? new ArrayList<>() : serializedProprietors;
    }

    /**
     * Will pack the owners of this object to the document.
     */
    default void packProprietors() {
        blobCrudable().getDocument().put("SharedSerializable#Proprietors", serializeProprietors());
    }

    /**
     * Will serialize the proprietors of this object in a way
     * that can later be deserialized, even if the object class
     * structure changes.
     *
     * @return The serialized proprietors.
     */
    default List<Map<String, Object>> serializeProprietors() {
        return getProprietors().values().stream()
                .map(SerializableProfile::serialize).toList();
    }

    /**
     * Used inside ProxiedSharedSerializableManager to join a player to a cached
     * object in a running session.
     *
     * @param player The player to join.
     */
    void join(Player player);
}
