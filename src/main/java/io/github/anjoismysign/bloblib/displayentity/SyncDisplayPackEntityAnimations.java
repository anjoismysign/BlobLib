package io.github.anjoismysign.bloblib.displayentity;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;

public class SyncDisplayPackEntityAnimations<T extends Display, R extends Cloneable> extends SyncDisplayEntityAnimations {
    private final PackMaster<?> packMaster;
    private final int index;

    public SyncDisplayPackEntityAnimations(DisplayEntity<?, ?> pet,
                                           EntityAnimationsCarrier carrier,
                                           PackMaster<?> packMaster,
                                           int index) {
        super(pet, carrier);
        this.packMaster = packMaster;
        this.index = index;
    }

    @Override
    public Location move(Player player, Location loc) {
        Location pivot = packMaster.pivot(player, index);
        pivot.setPitch(Location.normalizePitch(0.0f));
        pivot.setY(player.getLocation().getY() + yOffset);
        pet.teleport(pivot);
        return pivot;
    }

}
