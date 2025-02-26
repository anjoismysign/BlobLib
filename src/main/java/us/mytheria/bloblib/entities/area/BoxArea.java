package us.mytheria.bloblib.entities.area;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

public record BoxArea(@NotNull BoundingBox getBoundingBox,
                      @NotNull String getWorldName) implements Area {

    @Override
    public @NotNull AreaType getType() {
        return AreaType.BOX_AREA;
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
