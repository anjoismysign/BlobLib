package us.mytheria.bloblib.enginehub.worldguard;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public class Absent implements WorldGuardWorker {
    @Override
    public RegionContainer regionContainer() {
        return null;
    }

    @Override
    public RegionManager regionManager(World world) {
        return null;
    }

    @Override
    public ProtectedCuboidRegion protectedCuboidRegion(String id, boolean isTransient, Location min, Location max) {
        return null;
    }

    @Override
    public ProtectedPolygonalRegion protectedPolygonalRegion(String id, boolean isTransient, List<Location> points, int minY, int maxY) {
        return null;
    }

    @Override
    public ProtectedRegion getRegion(World world, String id) {
        return null;
    }
}
