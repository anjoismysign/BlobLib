package io.github.anjoismysign.bloblib.entities.area;

import io.github.anjoismysign.bloblib.entities.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
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

    @Override
    public void fill(@NotNull BlockData blockData) {
        @Nullable World world = Bukkit.getWorld(getWorldName);
        if (world == null){
            return;
        }
        Cuboid cuboid = Cuboid.of(getBoundingBox.getMin(), getBoundingBox.getMax(), world);
        cuboid.forEachBlock(block -> block.setBlockData(blockData));
    }
}
