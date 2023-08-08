package us.mytheria.bloblib.displayentity;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import us.mytheria.bloblib.BlobLib;

public class SyncDisplayEntityAnimations {
    private static final BlobLib plugin = BlobLib.getInstance();

    private final double followSpeed, walkAwaySpeed, hoverSpeed, hoverMaxHeightCap,
            hoverMinHeightCap, yOffset;
    private double hoverVelocity, hoverHeight;
    private final DisplayEntity<?, ?> pet;

    public SyncDisplayEntityAnimations(DisplayEntity<?, ?> pet, double followSpeed, double walkAwaySpeed, double hoverSpeed,
                                       double hoverMaxHeightCap, double hoverMinHeightCap,
                                       double yOffset) {
        this.pet = pet;
        this.followSpeed = followSpeed;
        this.walkAwaySpeed = walkAwaySpeed;
        this.hoverSpeed = hoverSpeed;
        this.hoverMaxHeightCap = hoverMaxHeightCap;
        this.hoverMinHeightCap = hoverMinHeightCap;
        this.hoverVelocity = hoverSpeed;
        this.hoverHeight = 1;
        this.yOffset = yOffset;
    }

    public SyncDisplayEntityAnimations(DisplayEntity<?, ?> pet, double followSpeed,
                                       double walkAwaySpeed, EntityAnimationsCarrier carrier) {
        this(pet, followSpeed, walkAwaySpeed, carrier.hoverSpeed(),
                carrier.hoverMaxHeightCap(), carrier.hoverMinHeightCap(),
                carrier.yOffset());
    }

    public Location move(Player player, Location loc) {
        Vector goal = vectorFromLocation(player.getLocation());
        goal.setY(goal.getY() + yOffset);
        double distance = Math.sqrt(Math.pow(loc.getX() - player.getLocation().getX(), 2) + Math.pow(loc.getZ() - player.getLocation().getZ(), 2));
        if (distance < 2.5D) {
            goal.setY(goal.getY() + player.getLocation().getY() - loc.getY());
            goal.setX(loc.getX());
            goal.setZ(loc.getZ());
        }
        Vector start = vectorFromLocation(loc);
        Vector direction = normalize(goal.subtract(start));
        Location newLoc = loc.clone();
        newLoc.add(direction.multiply(followSpeed));
        //Rotation
        double a = player.getLocation().getX() - newLoc.getX();
        double b = player.getLocation().getZ() - newLoc.getZ();
        double angle = Math.atan(b / a);
        angle = angle * (180 / Math.PI);
        if (player.getLocation().getX() - newLoc.getX() >= 0) {
            angle += 180;
        }
        angle += 90;
        newLoc.setYaw((float) angle);
        pet.teleport(newLoc);
        return newLoc;
    }

    public Location idle(Player player, Location loc) {
        //Hover
        if (hoverHeight >= hoverMaxHeightCap)
            hoverVelocity = -hoverSpeed;
        if (hoverHeight <= hoverMinHeightCap)
            hoverVelocity = hoverSpeed;
        Location newLoc = loc.clone();
        hoverHeight += hoverVelocity;
        newLoc.setY(newLoc.getY() + hoverVelocity);
        //Rotation
        double a = player.getLocation().getX() - newLoc.getX();
        double b = player.getLocation().getZ() - newLoc.getZ();
        double angle = Math.atan(b / a);
        angle = angle * (180 / Math.PI);
        if (player.getLocation().getX() - newLoc.getX() >= 0) {
            angle += 180;
        }
        angle += 90;
        newLoc.setYaw((float) angle);
        pet.teleport(newLoc);
        return newLoc;
    }

    public Location moveAway(Player player, Location loc) {
        Vector goal = vectorFromLocation(player.getLocation());
        Vector start = vectorFromLocation(loc);
        Vector direction = normalize(goal.subtract(start).multiply(-1));
        Location newLoc = loc.clone();
        newLoc.add(direction.multiply(walkAwaySpeed));
        newLoc.setY(loc.getY());
        //Rotation
        double a = player.getLocation().getX() - newLoc.getX();
        double b = player.getLocation().getZ() - newLoc.getZ();
        double angle = Math.atan(b / a);
        angle = angle * (180 / Math.PI);
        if (player.getLocation().getX() - newLoc.getX() >= 0) {
            angle += 180;
        }
        angle += 90;
        newLoc.setYaw((float) angle);
        pet.teleport(newLoc);
        return newLoc;
    }

    protected Vector vectorFromLocation(Location location) {
        return location.toVector();
    }

    protected Vector normalize(Vector vec) {
        return new Vector(vec.getX() / vec.length(), vec.getY() / vec.length(), vec.getZ() / vec.length());
    }
}
