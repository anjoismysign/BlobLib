package us.mytheria.bloblib.enginehub.worldguard;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface WorldGuardWorker {
    /**
     * Gets the region container
     *
     * @return the region container (cast to RegionContainer)
     */
    @Nullable
    Object regionContainer();

    /**
     * Gets the region manager for the given world
     *
     * @param world the world
     * @return the region manager (cast to RegionManager)
     */
    @Nullable
    Object regionManager(World world);

    /**
     * Creates a protected cuboid region
     *
     * @param id          the region id
     * @param isTransient whether the region is transient
     * @param min         the minimum location
     * @param max         the maximum location
     * @return the protected cuboid region (cast to ProtectedCuboidRegion)
     */
    @Nullable
    Object protectedCuboidRegion(String id, boolean isTransient,
                                 Location min, Location max);

    /**
     * Creates a protected polygonal region
     *
     * @param id          the region id
     * @param isTransient whether the region is transient
     * @param points      the points
     * @param minY        the minimum y
     * @param maxY        the maximum y
     * @return the protected polygonal region (cast to ProtectedPolygonalRegion)
     */
    @Nullable
    Object protectedPolygonalRegion(String id, boolean isTransient,
                                    List<Location> points,
                                    int minY, int maxY);

    /**
     * Gets the region with the given id in the given world
     *
     * @param world the world
     * @param id    the id
     * @return the region (cast to ProtectedRegion)
     */
    @Nullable
    Object getRegion(World world, String id);
}
