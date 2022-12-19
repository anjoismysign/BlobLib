package us.mytheria.bloblib.enginehub.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.World;

public interface WorldEditWorker {
    EditSession editSession(World world);

    com.sk89q.worldedit.world.World world(World world);

    Pattern parse(String string);

    boolean setBlocks(EditSession session, Region region, Pattern pattern);

    CuboidRegion cuboidRegion(Location min, Location max);

    BlockVector3 blockVector3(Location location);

    BlockVector2 blockVector2(Location location);
}
