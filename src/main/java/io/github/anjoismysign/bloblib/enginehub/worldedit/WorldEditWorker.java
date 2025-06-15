package io.github.anjoismysign.bloblib.enginehub.worldedit;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public interface WorldEditWorker {
    /**
     * Creates an EditSession for the given world
     *
     * @param world the world
     * @return the EditSession (cast to EditSession)
     */
    @Nullable
    Object editSession(World world);

    /**
     * Converts a Bukkit world to a WorldEdit world
     *
     * @param world the Bukkit world
     * @return the WorldEdit world (cast to com.sk89q.worldedit.world.World)
     */
    @Nullable
    Object world(World world);

    /**
     * Creates a pattern
     *
     * @param string The string to use
     * @return The pattern (cast to Pattern)
     */
    @Nullable
    Object pattern(String string);

    /**
     * Sets the blocks in the given region to the given pattern
     *
     * @param session the EditSession
     * @param region  the region
     * @param pattern the pattern
     * @return true if the operation was successful
     */
    boolean setBlocks(Object session, Object region, Object pattern);

    /**
     * Creates a cuboid region from the given locations
     *
     * @param min the minimum location
     * @param max the maximum location
     * @return the cuboid region (cast to CuboidRegion)
     */
    @Nullable
    Object cuboidRegion(Location min, Location max);

    /**
     * Creates a block vector from the given location
     *
     * @param location the location
     * @return the block vector (cast to BlockVector3)
     */
    @Nullable
    Object blockVector3(Location location);

    /**
     * Creates a block vector from the given location
     *
     * @param location the location
     * @return the block vector (cast to BlockVector2)
     */
    @Nullable
    Object blockVector2(Location location);
}
