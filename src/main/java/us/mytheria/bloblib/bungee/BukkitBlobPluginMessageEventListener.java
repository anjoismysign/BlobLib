package us.mytheria.bloblib.bungee;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.message.BlobPluginMessage;

public class BukkitBlobPluginMessageEventListener implements PluginMessageListener {
    private final BlobLib main;

    public BukkitBlobPluginMessageEventListener() {
        this.main = BlobLib.getInstance();
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] bytes) {
        BlobPluginMessage message = BlobPluginMessage.deserialize(bytes);
        if (message == null)
            return;
        main.getServer().getPluginManager().callEvent(new BukkitBlobPluginMessageEvent(message));
    }
}
