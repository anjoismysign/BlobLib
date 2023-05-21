package us.mytheria.bloblib.displayentity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 * A display entity is an entity which is not a real entity
 * in terms such as not having a hitbox, having a very
 * basic AI while allowing adding new custom entities
 * without having to make use of resource packs.
 * For example, FloatingPets are display entities.
 *
 * @param <T> the entity type which is the representation
 *            of the FloatingDisplay in the minecraft world
 *            or in case of complex display entities, the
 *            root parent part/entity.
 */
public interface DisplayEntity<T extends Entity> {

    /**
     * Spawns the entity.
     */
    void spawn();

    /**
     * Similar to Entity#remove.
     * Will mark the entity as deactivated and will remove the
     * floating display entity.
     */
    void destroy();

    /**
     * Returns the entity which is the representation
     * of the FloatingDisplay in the minecraft world
     * or in case of complex display entities, the
     * root parent part/entity.
     *
     * @return the entity
     */
    T getEntity();

    /**
     * Teleports the entity to the given location.
     *
     * @param location - the new location
     */
    void teleport(Location location);

    /**
     * Returns the location of the entity's entity.
     *
     * @return the location
     */
    Location getLocation();

    /**
     * Returns if the entity is activated.
     * If true, it means that it is spawned.
     * If false, it means that it was called
     * to be removed.
     *
     * @return if the entity is activated
     */
    boolean isActive();

    /**
     * Returns if the entity logic is paused
     *
     * @return if the logic is paused
     */
    boolean isPauseLogic();

    /**
     * Sets if the entity logic should be paused
     *
     * @param pauseLogic - if the logic should be paused
     *                   if true, will make entity static/paused
     */
    void setPauseLogic(boolean pauseLogic);

    /**
     * Sets the entity display/ItemStack
     *
     * @param blockData - the new head
     */
    void setDisplay(ItemStack blockData);
}
