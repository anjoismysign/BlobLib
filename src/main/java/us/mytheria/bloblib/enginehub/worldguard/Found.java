package us.mytheria.bloblib.enginehub.worldguard;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;
import us.mytheria.bloblib.enginehub.EngineHubAPI;

import java.util.ArrayList;
import java.util.List;

public class Found implements WorldGuardWorker {
    @Override
    public RegionContainer regionContainer() {
        return WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    @Override
    public RegionManager regionManager(World world) {
        return regionContainer().get(EngineHubAPI.world(world));
    }

    @Override
    public ProtectedCuboidRegion protectedCuboidRegion(String id, boolean isTransient,
                                                       Location min, Location max) {
        return new ProtectedCuboidRegion(id, isTransient,
                EngineHubAPI.blockVector3(min), EngineHubAPI.blockVector3(max));
    }

    @Override
    public ProtectedPolygonalRegion protectedPolygonalRegion(String id, boolean isTransient,
                                                             List<Location> points, int minY,
                                                             int maxY) {
        List<BlockVector2> list = new ArrayList<>();
        for (Location point : points) {
            list.add(EngineHubAPI.blockVector2(point));
        }
        return new ProtectedPolygonalRegion(id, isTransient, list, minY, maxY);
    }

    @Override
    public ProtectedRegion getRegion(World world, String id) {
        RegionManager manager = regionManager(world);
        return manager.getRegion(id);
    }
}
