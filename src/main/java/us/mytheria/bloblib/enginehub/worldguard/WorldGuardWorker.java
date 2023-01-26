package us.mytheria.bloblib.enginehub.worldguard;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public interface WorldGuardWorker {
    /**
     * Gets the region container
     *
     * @return the region container
     */
    RegionContainer regionContainer();

    /**
     * Gets the region manager for the given world
     *
     * @param world the world
     * @return the region manager
     */
    RegionManager regionManager(World world);

    /**
     * Creates a protected cuboid region
     *
     * @param id          the region id
     * @param isTransient whether the region is transient
     * @param min         the minimum location
     * @param max         the maximum location
     * @return the protected cuboid region
     */
    ProtectedCuboidRegion protectedCuboidRegion(String id, boolean isTransient,
                                                Location min, Location max);

    /**
     * Creates a protected polygonal region
     *
     * @param id          the region id
     * @param isTransient whether the region is transient
     * @param points      the points
     * @param minY        the minimum y
     * @param maxY        the maximum y
     * @return the protected polygonal region
     */
    ProtectedPolygonalRegion protectedPolygonalRegion(String id, boolean isTransient,
                                                      List<Location> points,
                                                      int minY, int maxY);

    /**
     * Gets the region with the given id in the given world
     *
     * @param world the world
     * @param id    the id
     * @return the region
     */
    ProtectedRegion getRegion(World world, String id);
}
