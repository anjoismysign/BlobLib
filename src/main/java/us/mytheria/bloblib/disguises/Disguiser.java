package us.mytheria.bloblib.disguises;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface Disguiser {
    /**
     * Will return disguise engine used by this server
     *
     * @return Disguise engine
     */
    DisguiseEngine getEngine();

    /**
     * Will disguise specific entity with provided disguise
     * to all players.
     *
     * @param disguise Disguise to use
     * @param entity   Entity to disguise
     */
    void disguiseEntity(String disguise, Entity entity);

    /**
     * Will disguise specific entity with provided disguise
     * to provided player/s only
     *
     * @param disguise Disguise to use
     * @param entity   Entity to disguise
     * @param target   Player to disguise to
     */
    void disguiseEntityForTarget(String disguise, Entity entity, Player... target);

    /**
     * True if server has disguise engine
     *
     * @return True if server has disguise engine
     */
    default boolean hasEngine() {
        return getEngine() != DisguiseEngine.NONE;
    }
}
