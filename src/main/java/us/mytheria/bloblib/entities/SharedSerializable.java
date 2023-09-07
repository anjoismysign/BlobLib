package us.mytheria.bloblib.entities;

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
    Map<String, T> getProprietors();

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
