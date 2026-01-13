package io.github.anjoismysign.bloblib.entities.area;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.anjoismysign.bloblib.middleman.enginehub.EngineHubManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public record WorldGuardArea(@NotNull String worldName,
                             @NotNull String id,
                             @Nullable BlockVector blockVectorCenter) implements Area {

    /**
     * Instances a WorldGuard Area.
     * It's implied that before calling this method, it needs to check that WorldGuard is installed.
     *
     * @param worldName The world the ProtectedRegion belongs to
     * @param id        The ProtectedRegion's ID
     * @return The WorldGuardArea
     */
    public static WorldGuardArea of(
            @NotNull String worldName,
            @NotNull String id,
            @Nullable BlockVector center) {
        Objects.requireNonNull(worldName, "'worldName' cannot be null");
        Objects.requireNonNull(id, "'id' cannot be null");
        return new WorldGuardArea(worldName, id, center);
    }

    @NotNull
    private ProtectedRegion getProtectedRegion() {
        Object result = EngineHubManager.getInstance()
                .getWorldGuardWorker()
                .getRegion(getWorld(), id);
        Objects.requireNonNull(result, "Couldn't find " + id + " in: " + worldName);
        return (ProtectedRegion) result;
    }

    @Override
    public @NotNull AreaType getType() {
        return AreaType.WORLD_GUARD_AREA;
    }

    @NotNull
    public String getWorldName() {
        return worldName;
    }

    @Override
    public @Nullable Location getCenter() {
        if (blockVectorCenter != null)
            return null;
        return new Location(getWorld(), blockVectorCenter.getX(), blockVectorCenter.getY(), blockVectorCenter.getZ());
    }

    @NotNull
    public String getId() {
        return id;
    }

    @Override
    public boolean isInside(@NotNull Location location) {
        World locationWorld = location.getWorld();
        return locationWorld != null && locationWorld.getName().equals(worldName)
                && getProtectedRegion().contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public void fill(@NotNull BlockData blockData) {
        ProtectedRegion protectedRegion = getProtectedRegion();
        @Nullable World world = Bukkit.getWorld(worldName);
        if (world == null){
            return;
        }
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        BlockState blockState = BukkitAdapter.adapt(blockData);
        Region region;
        switch (protectedRegion.getType()){
            case CUBOID -> {
                ProtectedCuboidRegion cuboid = (ProtectedCuboidRegion) protectedRegion;
                BlockVector3 min = cuboid.getMinimumPoint();
                BlockVector3 max = cuboid.getMaximumPoint();
                region = new CuboidRegion(weWorld, min, max);
            }
            case POLYGON -> {
                ProtectedPolygonalRegion poly = (ProtectedPolygonalRegion) protectedRegion;
                List<BlockVector2> wePoints = poly.getPoints();
                int minY = poly.getMinimumPoint().getY();
                int maxY = poly.getMaximumPoint().getY();
                region = new Polygonal2DRegion(weWorld, wePoints, minY, maxY);
            }
            default -> {
                return;
            }
        }
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
            editSession.setBlocks(region, blockState);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
