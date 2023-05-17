package us.mytheria.bloblib.floatingpet;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface FloatingPet<T extends Entity> {

    /**
     * Spawns the pet.
     */
    void spawn();

    /**
     * Will set pet's custom name.
     * If passing null, will be used 'owner's Pet'
     *
     * @param name - the custom name
     */
    void rename(String name);

    /**
     * Similar to Entity#remove.
     * Will mark the pet as deactivated and will remove the armorstand.
     */
    void destroy();

    /**
     * Returns the pet itemStack.
     * Not to confuse with the engine/entity!
     * The itemStack is what's being used by the
     * engine/entity as a itemStack.
     *
     * @return the itemStack
     */
    ItemStack getDisplay();

    /**
     * Returns the owner of the pet
     *
     * @return the owner
     */
    Player getOwner();

    /**
     * Sets the owner of the pet.
     * If different from the previous owner,
     * should make the pet follow the new owner!
     *
     * @param owner - the new owner
     */
    void setOwner(Player owner);

    /**
     * Returns the entity which is the representation
     * of the pet in the minecraft world.
     *
     * @return the entity
     */
    T getEntity();

    /**
     * Retrieves pet's name.
     *
     * @return if customName is null, returns 'owner's Pet', else returns customName
     */
    String getCustomName();

    /**
     * Teleports the pet to the given location.
     *
     * @param location - the new location
     */
    void teleport(Location location);

    /**
     * Returns the location of the pet's entity.
     *
     * @return the location
     */
    Location getLocation();

    /**
     * Returns if the pet is activated.
     * If true, it means that it is spawned.
     * If false, it means that it was called
     * to be removed.
     *
     * @return if the pet is activated
     */
    boolean isActive();

    /**
     * Returns if the pet logic is paused
     *
     * @return if the logic is paused
     */
    boolean isPauseLogic();

    /**
     * Sets if the pet logic should be paused
     *
     * @param pauseLogic - if the logic should be paused
     *                   if true, will make pet static/paused
     */
    void setPauseLogic(boolean pauseLogic);

    /**
     * Returns the particle of the pet
     *
     * @return the particle
     */
    @Nullable
    Particle getParticle();

    /**
     * Sets the particle of the pet
     *
     * @param particle - the new particle
     *                 if null, the pet will not have a particle
     */
    void setParticle(Particle particle);

    /**
     * Sets the pet head
     *
     * @param display - the new head
     */
    void setDisplay(ItemStack display);
}
