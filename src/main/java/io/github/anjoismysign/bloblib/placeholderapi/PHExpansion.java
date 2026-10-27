package io.github.anjoismysign.bloblib.placeholderapi;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A PlaceholderAPI expansion that placeholders can be added to.
 * Free of PlaceholderAPI types, so it is safe to reference
 * even when PlaceholderAPI is not installed.
 */
public interface PHExpansion {

    /**
     * @return the previous value associated with key, or null if there was no mapping for key. (A null return can also indicate that the map previously associated null with key, if the implementation supports null values.)
     */
    Function<OfflinePlayer, String> putSimple(String key, Function<OfflinePlayer, String> function);

    BiFunction<Player, Player, String> putSimpleRelational(String key, BiFunction<Player, Player, String> biFunction);

    /**
     * @return the previous value associated with key, or null if there was no mapping for key. (A null return can also indicate that the map previously associated null with key, if the implementation supports null values.)
     */
    BiFunction<OfflinePlayer, String, String> putStartsWith(String key, BiFunction<OfflinePlayer, String, String> function);
}
