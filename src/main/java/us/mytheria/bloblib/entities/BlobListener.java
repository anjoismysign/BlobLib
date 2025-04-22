package us.mytheria.bloblib.entities;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import us.mytheria.bloblib.BlobLib;

public interface BlobListener extends Listener {
    
    /**
     * Will reload the listener.
     * It will register if needed or unregister otherwise.
     */
    default void reload() {
        unregister();
        if (checkIfShouldRegister())
            register();
    }

    /**
     * Will unregister the listener.
     */
    default void unregister() {
        HandlerList.unregisterAll(this);
    }

    /**
     * Will register the listener.
     * Note: This will not check if the listener should be registered.
     */
    default void register() {
        Bukkit.getPluginManager().registerEvents(this, BlobLib.getInstance());
    }

    /**
     * Will check if the listener should be registered.
     *
     * @return true if the listener should be registered, false otherwise.
     */
    boolean checkIfShouldRegister();
}
