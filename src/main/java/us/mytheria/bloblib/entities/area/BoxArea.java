package us.mytheria.bloblib.entities.area;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record BoxArea(@NotNull BoundingBox getBoundingBox,
                      @NotNull String getWorldName,
                      @Nullable BlockVector blockVectorCenter) implements Area {

    @Override
    public @NotNull AreaType getType() {
        return AreaType.BOX_AREA;
    }

    @Override
    public @Nullable Location getCenter() {
        if (blockVectorCenter == null)
            return null;
        return new Location(getWorld(), blockVectorCenter.getX(), blockVectorCenter.getY(), blockVectorCenter.getZ());
    }

    public boolean contains(@NotNull BoxArea other) {
        return other.getBoundingBox.contains(getBoundingBox);
    }

    @Override
    public boolean isInside(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null)
            return false;
        if (!world.getName().equals(getWorldName))
            return false;
        return getBoundingBox.contains(location.toVector());
    }
}
