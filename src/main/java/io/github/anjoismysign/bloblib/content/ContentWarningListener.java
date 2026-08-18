package io.github.anjoismysign.bloblib.content;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.message.BlobMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Reports {@link ContentWarning}s once every plugin has loaded its content.
 * <p>
 * Third party plugins load their own assets during their onEnable, so the
 * earliest point at which the report is complete is the server having loaded,
 * not BlobLib having enabled.
 */
public class ContentWarningListener implements Listener {

    /**
     * The permission a player needs to be told about content warnings on join.
     */
    public static final String PERMISSION = "bloblib.content.operator";

    private static final String MESSAGE_KEY = "BlobLib.Content-Operator-Warnings";

    private final BlobLib plugin;

    public ContentWarningListener(@NotNull BlobLib plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void handle(ServerLoadEvent event) {
        ContentWarningRegistry registry = ContentWarningRegistry.getInstance();
        if (registry.isEmpty())
            return;
        registry.report().forEach(line -> BlobLib.getAnjoLogger().log(line));
    }

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission(PERMISSION))
            return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isConnected())
                return;
            int amount = ContentWarningRegistry.getInstance().size();
            if (amount < 1)
                return;
            @Nullable BlobMessage message = BlobMessage.by(MESSAGE_KEY);
            if (message == null)
                return;
            @Nullable BlobMessage localized = message.localize(player.getLocale());
            if (localized == null)
                localized = message;
            localized.modder()
                    .replace("%amount%", String.valueOf(amount))
                    .get()
                    .toCommandSender(player);
        }, 20L);
    }
}
