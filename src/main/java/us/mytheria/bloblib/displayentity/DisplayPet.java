package us.mytheria.bloblib.displayentity;

import org.bukkit.Location;
import org.bukkit.Nameable;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.Ownable;
import us.mytheria.bloblib.entities.ParticleContainer;

public interface DisplayPet<T extends Entity> extends DisplayEntity<T>,
        Nameable, ParticleContainer, Ownable {

    /**
     * Retrieves the current particle that FloatingPet emits.
     * If null, no particle is emitted.
     *
     * @return The current particle, or null if no particle is set.
     */
    @Nullable
    Particle getParticle();

    /**
     * Sets the particle that FloatingPet emits.
     * If particle is null, no particle must be emitted.
     *
     * @param particle The particle to be set.
     */
    void setParticle(Particle particle);

    /**
     * Will spawn particles at FloatingPet's location
     * if FloatingPet#getParticle() is not null.
     * Passes '0.7' offsetY and '0' count to spawnParticles(double, int).
     */
    default void spawnParticles() {
        spawnParticles(0.7, 0);
    }

    /**
     * Will spawn particles at FloatingPet's location
     * if FloatingPet#getParticle() is not null.
     *
     * @param offsetY The offset on the Y axis to spawn the particles.
     * @param count   The amount of particles to spawn.
     */
    default void spawnParticles(double offsetY, int count) {
        Particle particle = getParticle();
        if (particle == null)
            return;
        Location location = getLocation().clone();
        location.setY(location.getY() + offsetY);
        location.getWorld().spawnParticle(particle, location, count);
    }
}
