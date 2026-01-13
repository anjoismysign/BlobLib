package io.github.anjoismysign.bloblib.middleman.enginehub.worldguard;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

/**
 * A class that does nothing since WorldGuard is absent.
 */
public class Absent implements WorldGuardWorker {
    /**
     * Does nothing since WorldGuard is not present
     *
     * @return null
     */
    @Override
    public Object regionContainer() {
        return null;
    }

    /**
     * Does nothing since WorldGuard is not present
     *
     * @param world the world
     * @return null
     */
    @Override
    public Object regionManager(World world) {
        return null;
    }

    /**
     * Does nothing since WorldGuard is not present
     *
     * @param id          the region id
     * @param isTransient whether the region is transient
     * @param min         the minimum location
     * @param max         the maximum location
     * @return null
     */
    @Override
    public Object protectedCuboidRegion(String id, boolean isTransient, Location min, Location max) {
        return null;
    }

    /**
     * Does nothing since WorldGuard is not present
     *
     * @param id          the region id
     * @param isTransient whether the region is transient
     * @param points      the points
     * @param minY        the minimum y
     * @param maxY        the maximum y
     * @return null
     */
    @Override
    public Object protectedPolygonalRegion(String id, boolean isTransient, List<Location> points, int minY, int maxY) {
        return null;
    }

    /**
     * Does nothing since WorldGuard is not present
     *
     * @param world the world
     * @param id    the id
     * @return null
     */
    @Override
    public Object getRegion(World world, String id) {
        return null;
    }
}
