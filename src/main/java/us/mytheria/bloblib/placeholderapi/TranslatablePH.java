package us.mytheria.bloblib.placeholderapi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;
import us.mytheria.bloblib.entities.BlobPHExpansion;
import us.mytheria.bloblib.entities.translatable.TranslatableSnippet;

import java.util.Objects;

public class TranslatablePH {
    private static TranslatablePH instance;
    private BlobPHExpansion expansion;

    @NotNull
    public static TranslatablePH getInstance(@NotNull BlobLib plugin) {
        if (instance == null) {
            Objects.requireNonNull(plugin, "injected dependency is null");
            instance = new TranslatablePH(plugin);
        }
        return instance;
    }

    private TranslatablePH(@NotNull BlobLib plugin) {
        instance = this;
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            BlobLib.getAnjoLogger().log("PlaceholderAPI not found, not registering Translatabe PlaceholderAPI expansion");
            return;
        }
        BlobPHExpansion expansion = new BlobPHExpansion(plugin, "translatable");
        this.expansion = expansion;
        expansion.putStartsWith("snippet_", (offlinePlayer, key) -> {
            Player player = Bukkit.getPlayer(offlinePlayer.getUniqueId());
            TranslatableSnippet snippet;
            if (player == null)
                snippet = BlobLibTranslatableAPI.getInstance().getTranslatableSnippet(key);
            else
                snippet = BlobLibTranslatableAPI.getInstance().getTranslatableSnippet(key, player);
            if (snippet == null)
                return ChatColor.RED + key;
            return snippet.get();
        });
    }

    public BlobPHExpansion getExpansion() {
        return expansion;
    }
}
