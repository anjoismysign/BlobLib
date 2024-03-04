package us.mytheria.bloblib.displayentity;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import us.mytheria.bloblib.entities.SquareMaster;
import us.mytheria.bloblib.gists.RelativeLocation;

import java.util.Map;
import java.util.Objects;

public class PackMaster<T> extends SquareMaster<T> {
    private @NotNull Vector pivot;

    public static <T> PackMaster<T> of(int maxPerRow,
                                       double componentLength,
                                       @NotNull Map<Integer, T> components,
                                       @NotNull Vector pivot) {
        Objects.requireNonNull(components, "'components' cannot be null!");
        Objects.requireNonNull(pivot, "'pivot' cannot be null!");
        return new PackMaster<>(maxPerRow, componentLength, components, pivot);
    }

    private PackMaster(int maxPerRow,
                       double componentLength,
                       @NotNull Map<Integer, T> components,
                       @NotNull Vector pivot) {
        super(maxPerRow, componentLength, components);
        this.pivot = pivot;
    }

    @NotNull
    public Vector getPivot() {
        return pivot;
    }

    public Location pivot(Player player, int index) {
        Location location = player.getLocation().clone();
        location.setPitch(Location.normalizePitch(0));
        Vector2d offset = getOffset(index);
        return RelativeLocation.getInstance()
                .getRelativeLocation(location,
                        pivot.getX() - (offset.y * getComponentLength()),
                        pivot.getZ() + (offset.x * getComponentLength()),
                        pivot.getY());
    }

    public void setPivot(@NotNull Vector pivot) {
        Objects.requireNonNull(pivot, "'pivot' cannot be null!");
        this.pivot = pivot;
    }
}
