package us.mytheria.bloblib.entities.area;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.enginehub.EngineHubManager;

import java.util.Objects;

public class WorldGuardArea implements Area {
    private final World world;
    private final String id;

    /**
     * Instances a WorldGuard Area.
     * It's implied that before calling this method, it needs to check that WorldGuard is installed.
     *
     * @param world The world the ProtectedRegion belongs to
     * @param id    The ProtectedRegion's ID
     * @return The WorldGuardArea
     */
    public static WorldGuardArea of(
            @NotNull World world,
            @NotNull String id) {
        Objects.requireNonNull(world, "'world' cannot be null");
        Objects.requireNonNull(id, "'id' cannot be null");
        return new WorldGuardArea(world, id);
    }

    private WorldGuardArea(
            World world,
            String id) {
        this.world = world;
        this.id = id;
    }

    @NotNull
    private ProtectedRegion getProtectedRegion() {
        Object result = EngineHubManager.getInstance()
                .getWorldGuardWorker()
                .getRegion(world, id);
        Objects.requireNonNull(result, "Couldn't find " + id + " in: " + world);
        return (ProtectedRegion) result;
    }

    @Override
    public @NotNull World getWorld() {
        return world;
    }

    @Override
    public boolean isInside(@NotNull Location location) {
        World locationWorld = location.getWorld();
        return locationWorld != null && locationWorld.getName().equals(world.getName())
                && getProtectedRegion().contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
