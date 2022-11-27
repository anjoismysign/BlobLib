package us.mytheria.bloblib.utilities;

import org.bukkit.Bukkit;

public class Debug {
    public static void debug(String message) {
        Bukkit.getLogger().severe(message);
    }
}
