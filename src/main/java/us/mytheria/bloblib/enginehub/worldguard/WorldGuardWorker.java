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
    RegionContainer regionContainer();

    RegionManager regionManager(World world);

    ProtectedCuboidRegion protectedCuboidRegion(String id, boolean isTransient,
                                                Location min, Location max);

    ProtectedPolygonalRegion protectedPolygonalRegion(String id, boolean isTransient,
                                                      List<Location> points,
                                                      int minY, int maxY);

    ProtectedRegion getRegion(World world, String id);
}
