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
    /**
     * Creates an EditSession for the given world
     *
     * @param world the world
     * @return the EditSession
     */
    EditSession editSession(World world);

    /**
     * Converts a Bukkit world to a WorldEdit world
     *
     * @param world the Bukkit world
     * @return the WorldEdit world
     */
    com.sk89q.worldedit.world.World world(World world);

    /**
     * Converts a Bukkit location to a WorldEdit location
     *
     * @param string the Bukkit location
     * @return the WorldEdit location
     */
    Pattern parse(String string);

    /**
     * Sets the blocks in the given region to the given pattern
     *
     * @param session the EditSession
     * @param region  the region
     * @param pattern the pattern
     * @return true if the operation was successful
     */
    boolean setBlocks(EditSession session, Region region, Pattern pattern);

    /**
     * Creates a cuboid region from the given locations
     *
     * @param min the minimum location
     * @param max the maximum location
     * @return the cuboid region
     */
    CuboidRegion cuboidRegion(Location min, Location max);

    /**
     * Creates a block vector from the given location
     *
     * @param location the location
     * @return the block vector
     */
    BlockVector3 blockVector3(Location location);

    /**
     * Creates a block vector from the given location
     *
     * @param location the location
     * @return the block vector
     */
    BlockVector2 blockVector2(Location location);
}
