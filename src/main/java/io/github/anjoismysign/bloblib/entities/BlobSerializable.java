package io.github.anjoismysign.bloblib.entities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a BinarySerializable that is associated with a player
 */
public interface BlobSerializable extends BinarySerializable {
    /**
     * Will return the player that is associated
     * Might be null if called after the player has left
     * the session or if identification is not player name or uuid.
     *
     * @return the player
     */
    @Nullable
    default Player getPlayer() {
        int length = blobCrudable().getIdentification().length();
        if (length == 36) {
            return Bukkit.getPlayer(UUID.fromString(blobCrudable().getIdentification()));
        } else {
            return Bukkit.getPlayerExact(blobCrudable().getIdentification());
        }
    }
}
