package io.github.anjoismysign.bloblib.entities.area;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.anjoismysign.bloblib.enginehub.EngineHubManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
}
