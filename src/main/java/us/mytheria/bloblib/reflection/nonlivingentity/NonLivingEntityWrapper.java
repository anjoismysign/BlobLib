package us.mytheria.bloblib.reflection.nonlivingentity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface NonLivingEntityWrapper {

    /**
     * Will teleport the entity to the location including passengers
     * without ejecting them. This allows a smooth client-side interpolation
     * which simulates that both vehicle and passenger are moving.
     *
     * @param vehicle  the entity in which the passengers are in
     * @param location the location to teleport the entity to
     */
    void vehicleTeleport(Entity vehicle, Location location);
}
