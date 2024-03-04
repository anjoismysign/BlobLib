package us.mytheria.bloblib.gists;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Wasabi_Thumbs (<a href="https://www.spigotmc.org/threads/getting-location-to-the-relative-left-or-right-of-player.580325/">...</a>)
 */
public class RelativeLocation {
    private static RelativeLocation instance;

    public static RelativeLocation getInstance() {
        if (instance == null) {
            instance = new RelativeLocation();
        }
        return instance;
    }

    private final double epsilon = Math.ulp(1.0d) * 2d;

    private boolean isSignificant(double value) {
        return Math.abs(value) >= epsilon;
    }

    public Location getRelativeLocation(@NotNull Location location,
                                        double forward,
                                        double right) {
        return getRelativeLocation(location, forward, right, 0d);
    }

    public Location getRelativeLocation(@NotNull Location location,
                                        double forward,
                                        double right,
                                        double up) {
        Objects.requireNonNull(location, "'location' cannot be null!");
        Vector direction = null;
        if (isSignificant(forward)) {
            direction = location.getDirection();
            location.add(direction.clone().multiply(forward));
        }
        boolean hasUp = isSignificant(up);
        if (hasUp && direction == null) direction = location.getDirection();
        if (isSignificant(right) || hasUp) {
            Vector rightDirection;
            if (direction != null && isSignificant(Math.abs(direction.getY()) - 1)) {
                rightDirection = direction.clone();
                double factor = Math.sqrt(1 - Math.pow(rightDirection.getY(), 2)); // a shortcut that lets us not normalize which is slow
                double nx = -rightDirection.getZ() / factor;
                double nz = rightDirection.getX() / factor;
                rightDirection.setX(nx);
                rightDirection.setY(0d);
                rightDirection.setZ(nz);
            } else {
                float yaw = location.getYaw() + 90f;
                double yawRad = yaw * (Math.PI / 180d);
                double z = Math.cos(yawRad);
                double x = -Math.sin(yawRad);
                rightDirection = new Vector(x, 0d, z);
            }
            location.add(rightDirection.clone().multiply(right));
            if (hasUp) {
                Vector upDirection = rightDirection.crossProduct(direction);
                location.add(upDirection.clone().multiply(up));
            }
        }
        return location;
    }
}
