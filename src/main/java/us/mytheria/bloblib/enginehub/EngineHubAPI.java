package us.mytheria.bloblib.enginehub;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import us.mytheria.bloblib.BlobLib;

import java.util.List;

public class EngineHubAPI {

    public static World world(org.bukkit.World world) {
        return BlobLib.getInstance().getEngineHubManager().getWorldEditWorker().world(world);
    }

    public static BlockVector3 blockVector3(Location location) {
        return BlobLib.getInstance().getEngineHubManager().getWorldEditWorker().blockVector3(location);
    }

    public static BlockVector2 blockVector2(Location location) {
        return BlobLib.getInstance().getEngineHubManager().getWorldEditWorker().blockVector2(location);
    }

    public static EditSession editSession(org.bukkit.World world) {
        return BlobLib.getInstance().getEngineHubManager().getWorldEditWorker().editSession(world);
    }

    public static Pattern parse(String string) {
        return BlobLib.getInstance().getEngineHubManager().getWorldEditWorker().parse(string);
    }

    public static boolean setBlocks(EditSession session, com.sk89q.worldedit.regions.Region region, Pattern pattern) {
        return BlobLib.getInstance().getEngineHubManager().getWorldEditWorker().setBlocks(session, region, pattern);
    }

    public static com.sk89q.worldedit.regions.CuboidRegion cuboidRegion(Location min, Location max) {
        return BlobLib.getInstance().getEngineHubManager().getWorldEditWorker().cuboidRegion(min, max);
    }

    public static RegionContainer regionContainer() {
        return BlobLib.getInstance().getEngineHubManager().getWorldGuardWorker().regionContainer();
    }

    public static RegionManager regionManager(org.bukkit.World world) {
        return BlobLib.getInstance().getEngineHubManager().getWorldGuardWorker().regionManager(world);
    }

    public static ProtectedCuboidRegion protectedCuboidRegion(String id, boolean isTransient,
                                                              Location min, Location max) {
        return BlobLib.getInstance().getEngineHubManager().getWorldGuardWorker().protectedCuboidRegion(id, isTransient, min, max);
    }

    public static ProtectedPolygonalRegion protectedPolygonalRegion(String id, boolean isTransient,
                                                                    List<Location> points, int minY, int maxY) {
        return BlobLib.getInstance().getEngineHubManager().getWorldGuardWorker().protectedPolygonalRegion(id, isTransient, points,
                minY, maxY);
    }

    public static ProtectedRegion getRegion(org.bukkit.World world, String id) {
        return BlobLib.getInstance().getEngineHubManager().getWorldGuardWorker().getRegion(world, id);
    }
}
