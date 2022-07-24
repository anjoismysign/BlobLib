package us.mytheria.bloblib.utilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class BukkitUtil {

    /**
     * Converts a Location object to String so later can be converted back to Location.
     *
     * @see Location
     */
    public static String locationToString(Location loc) {
        return loc.getWorld().getName() + "%loc:%" + loc.getX() + "%loc:%" + loc.getY() + "%loc:%" + loc.getZ();
    }

    /**
     * Converts String that were made with locationToString into Location object.
     *
     * @see Location
     */
    public static Location stringToLocation(String string) {
        String[] split = string.split("%loc:%");
        return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]),
                Double.parseDouble(split[3]));
    }

    /**
     * Prints Location in a more readable way.
     *
     * @see Location
     */
    public static String printLocation(Location location) {
        return location.getWorld().getName() + "-" + location.toVector() + " (World-X,Y,Z)";
    }
}
