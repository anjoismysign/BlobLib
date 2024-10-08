package us.mytheria.bloblib.entities.area;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.enginehub.EngineHubManager;

import java.util.Objects;

public class WorldGuardArea implements Area {
    private final String worldName;
    private final String id;

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
            @NotNull String id) {
        Objects.requireNonNull(worldName, "'worldName' cannot be null");
        Objects.requireNonNull(id, "'id' cannot be null");
        return new WorldGuardArea(worldName, id);
    }

    private WorldGuardArea(
            String worldName,
            String id) {
        this.worldName = worldName;
        this.id = id;
    }

    @NotNull
    private ProtectedRegion getProtectedRegion() {
        Object result = EngineHubManager.getInstance()
                .getWorldGuardWorker()
                .getRegion(getWorld(), id);
        Objects.requireNonNull(result, "Couldn't find " + id + " in: " + worldName);
        return (ProtectedRegion) result;
    }

    @NotNull
    public String getWorldName() {
        return worldName;
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
