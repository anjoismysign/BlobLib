package us.mytheria.bloblib.entities;

import org.bukkit.Particle;

/**
 * Represents a container for particles.
 * Provides methods to get and set particles.
 */
public interface ParticleContainer {

    /**
     * Retrieves the current particle.
     *
     * @return The current particle.
     */
    Particle getParticle();

    /**
     * Sets the particle within the container.
     *
     * @param particle The particle to be set.
     */
    void setParticle(Particle particle);
}
