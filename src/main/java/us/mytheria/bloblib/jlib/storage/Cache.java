package us.mytheria.bloblib.jlib.storage;

import org.bukkit.entity.Player;
import us.mytheria.bloblib.jlib.storage.database.CallbackHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 18/01/16
 */
public abstract class Cache<C> {
    private final Map<Player, C> cache = new HashMap<>();

    /**
     * Loads a player into the Cache
     * @param player The player
     */
    public final void load(final Player player) {
        this.existsOffline(player.getUniqueId().toString(), new CallbackHandler<Boolean>() {
            @Override
            public void callback(Boolean o) {
                if(o) {
                    Cache.this.getOffline(player.getUniqueId().toString(), new CallbackHandler<C>() {
                        @Override
                        public void callback(C o) {
                            Cache.this.cache.put(player, o);
                        }
                    });
                } else Cache.this.cache.put(player, Cache.this.createOffline(player.getUniqueId().toString()));
            }
        });
    }

    /**
     * Unloads a player from the Cache
     * @param player The player to unload
     */
    public final void unload(Player player) {
        C c = this.cache.get(player);
        this.cache.remove(player);
        this.setOffline(player.getUniqueId().toString(), c);
    }

    /**
     * Returns the cached value of a player
     * @param player The player
     * @return The cached value
     */
    public final C get(Player player) {
        return this.cache.get(player);
    }

    /**
     * Sets the cached value of a player
     * @param player The player
     * @param c The cached value
     */
    public final void set(Player player, C c) {
        this.cache.put(player, c);
    }

    /**
     * Returns whether a player is loaded into the Cache
     * @param player The player
     * @return Whether the player is loaded
     */
    public final boolean isLoaded(Player player) { return this.cache.containsKey(player); }

    /**
     * Gets the offline value for a player
     * @param player The player
     * @param callbackHandler The CallbackHandler to call back to
     */
    public abstract void getOffline(String player, CallbackHandler<C> callbackHandler);

    /**
     * Sets the offline value for a player
     * @param player The player
     * @param c The value
     */
    public abstract void setOffline(String player, C c);

    /**
     * Checks whether an offline value exists for a player
     * @param player The player
     * @param callbackHandler The CallbackHandler to call back to
     */
    public abstract void existsOffline(String player, CallbackHandler<Boolean> callbackHandler);

    /**
     * Creates the offline value for a player
     * @param player The player
     * @return The value (Default value)
     */
    public abstract C createOffline(String player);
}
