package us.mytheria.bloblib.enginehub.worldguard;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
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
    public RegionContainer regionContainer() {
        return null;
    }

    /**
     * Does nothing since WorldGuard is not present
     *
     * @param world the world
     * @return null
     */
    @Override
    public RegionManager regionManager(World world) {
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
    public ProtectedCuboidRegion protectedCuboidRegion(String id, boolean isTransient, Location min, Location max) {
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
    public ProtectedPolygonalRegion protectedPolygonalRegion(String id, boolean isTransient, List<Location> points, int minY, int maxY) {
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
    public ProtectedRegion getRegion(World world, String id) {
        return null;
    }
}
