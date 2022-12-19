package us.mytheria.bloblib.enginehub.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.World;

public class Absent implements WorldEditWorker {
    @Override
    public EditSession editSession(World world) {
        return null;
    }

    @Override
    public com.sk89q.worldedit.world.World world(World world) {
        return null;
    }

    @Override
    public Pattern parse(String string) {
        return null;
    }

    @Override
    public boolean setBlocks(EditSession session, Region region, Pattern pattern) {
        return true;
    }

    @Override
    public CuboidRegion cuboidRegion(Location min, Location max) {
        return null;
    }

    @Override
    public BlockVector3 blockVector3(Location location) {
        return null;
    }

    @Override
    public BlockVector2 blockVector2(Location location) {
        return null;
    }
}
