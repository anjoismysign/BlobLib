package us.mytheria.bloblib.enginehub.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * A class that does nothing since WorldEdit is absent.
 */
public class Absent implements WorldEditWorker {
    /**
     * Does nothing since WorldEdit is absent.
     *
     * @param world The world to get the edit session from
     * @return null
     */
    @Override
    public EditSession editSession(World world) {
        return null;
    }

    /**
     * Does nothing since WorldEdit is absent.
     *
     * @param world The world to get the world from
     * @return null
     */
    @Override
    public com.sk89q.worldedit.world.World world(World world) {
        return null;
    }

    /**
     * Does nothing since WorldEdit is absent.
     *
     * @param string The string to parse
     * @return null
     */
    @Override
    public Pattern parse(String string) {
        return null;
    }

    /**
     * Does nothing since WorldEdit is absent.
     *
     * @param session The edit session to use
     * @param region  The region to set the blocks in
     * @param pattern The pattern to set the blocks to
     * @return true
     */
    @Override
    public boolean setBlocks(EditSession session, Region region, Pattern pattern) {
        return true;
    }

    /**
     * Does nothing since WorldEdit is absent.
     *
     * @param min minimum point
     * @param max maximum point
     * @return null
     */
    @Override
    public CuboidRegion cuboidRegion(Location min, Location max) {
        return null;
    }

    /**
     * Does nothing since WorldEdit is absent.
     *
     * @param location The location to get the block vector from
     * @return null
     */
    @Override
    public BlockVector3 blockVector3(Location location) {
        return null;
    }

    /**
     * Does nothing since WorldEdit is absent.
     *
     * @param location The location to get the block vector from
     * @return null
     */
    @Override
    public BlockVector2 blockVector2(Location location) {
        return null;
    }
}
