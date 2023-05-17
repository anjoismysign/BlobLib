package us.mytheria.bloblib.entities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Ownable {
    /**
     * Retrieves the current owner of the object.
     *
     * @return The current owner of the object.
     */
    UUID getOwner();

    /**
     * Sets a new owner for the object.
     *
     * @param newOwner The new owner of the object.
     */
    void setOwner(UUID newOwner);

    /**
     * Will attempt to retrieve the owner as a Bukkit Player.
     *
     * @return The owner as a Bukkit Player, null if not online.
     */
    @Nullable
    default Player findOwner() {
        return Bukkit.getPlayer(getOwner());
    }
}
