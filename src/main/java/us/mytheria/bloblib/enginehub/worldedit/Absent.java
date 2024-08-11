package us.mytheria.bloblib.enginehub.worldedit;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * A class that does nothing since WorldEdit is absent.
 */
public class Absent implements WorldEditWorker {
    /**
     * Does nothing since WorldEdit is absent.
     *
     * @param world The world to get the edit session from
     * @return null
     */
    @Override
    public Object editSession(World world) {
        return null;
    }

    /**
     * Does nothing since WorldEdit is absent.
     *
     * @param world The world to get the world from
     * @return null
     */
    @Override
    public Object world(World world) {
        return null;
    }

    /**
     * Does nothing since WorldEdit is absent.
     *
     * @param string The string to parse
     * @return null
     */
    @Override
    public Object pattern(String string) {
        return null;
    }

    /**
     * Does nothing since WorldEdit is absent.
     *
     * @param session The edit session to use
     * @param region  The region to set the blocks in
     * @param pattern The pattern to set the blocks to
     * @return true
     */
    @Override
    public boolean setBlocks(Object session, Object region, Object pattern) {
        return true;
    }

    /**
     * Does nothing since WorldEdit is absent.
     *
     * @param min minimum point
     * @param max maximum point
     * @return null
     */
    @Override
    public Object cuboidRegion(Location min, Location max) {
        return null;
    }

    /**
     * Does nothing since WorldEdit is absent.
     *
     * @param location The location to get the block vector from
     * @return null
     */
    @Override
    public Object blockVector3(Location location) {
        return null;
    }

    /**
     * Does nothing since WorldEdit is absent.
     *
     * @param location The location to get the block vector from
     * @return null
     */
    @Override
    public Object blockVector2(Location location) {
        return null;
    }
}
