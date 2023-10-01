package us.mytheria.bloblib.entities;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BlobPHExpansion extends PlaceholderExpansion {
    private final JavaPlugin plugin;
    private final String identifier;

    private final Map<String, Function<OfflinePlayer, String>> simple;
    private final Map<String, Function<OfflinePlayer, String>> startsWith;

    public BlobPHExpansion(JavaPlugin plugin, String identifier) {
        this.plugin = plugin;
        this.identifier = identifier.toLowerCase();
        simple = new HashMap<>();
        startsWith = new HashMap<>();
        Bukkit.getScheduler().runTask(plugin, this::register);
    }

    /**
     * @return the previous value associated with key, or null if there was no mapping for key. (A null return can also indicate that the map previously associated null with key, if the implementation supports null values.)
     */
    public Function<OfflinePlayer, String> putSimple(String key, Function<OfflinePlayer, String> function) {
        return simple.put(key, function);
    }

    /**
     * @return the previous value associated with key, or null if there was no mapping for key. (A null return can also indicate that the map previously associated null with key, if the implementation supports null values.)
     */
    public Function<OfflinePlayer, String> putStartsWith(String key, Function<OfflinePlayer, String> function) {
        return startsWith.put(key, function);
    }

    @Nullable
    private String getPluginAuthors() {
        List<String> authors = plugin.getDescription().getAuthors();
        if (authors.isEmpty())
            return null;
        return String.join(", ", authors);
    }

    public boolean canRegister() {
        return true;
    }

    public @NotNull String getAuthor() {
        return getPluginAuthors() == null ? "anjoismysign" : getPluginAuthors();
    }

    public @NotNull String getIdentifier() {
        return plugin.getName().toLowerCase() + identifier;
    }

    public @NotNull String getVersion() {
        return "1.0";
    }

    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        Function<OfflinePlayer, String> simpleFunction = simple.get(identifier);
        if (simpleFunction != null)
            return simpleFunction.apply(player);
        return startsWith.entrySet().stream()
                .filter(entry -> identifier.startsWith(entry.getKey()))
                .filter(entry -> entry.getValue().apply(player) != null)
                .findFirst()
                .map(entry -> entry.getValue().apply(player))
                .orElse(null);
    }
}