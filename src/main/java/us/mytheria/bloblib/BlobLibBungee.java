package us.mytheria.bloblib;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import us.mytheria.bloblib.bungee.PluginMessageEventListener;
import us.mytheria.bloblib.entities.message.BungeeMessage;

/**
 * BlobLib is planned to be a Bungeecord plugin as well.
 */
public class BlobLibBungee extends Plugin {
    private static BlobLibBungee instance;

    /**
     * Will retrieve the instance of the plugin
     *
     * @return the instance of the plugin
     */
    public static BlobLibBungee getInstance() {
        return instance;
    }

    /**
     * Called when the plugin is enabled
     */
    @Override
    public void onEnable() {
        instance = this;
        this.getProxy().registerChannel("BlobLib");
        new PluginMessageEventListener();
    }

    /**
     * Called when the plugin is disabled
     */
    @Override
    public void onDisable() {
    }

    /**
     * Send data by any available means to this server.
     *
     * @param server  the server to send this data via
     * @param message the message to send
     * @param queue   hold the message for later sending if it cannot be sent
     *                immediately.
     * @return true if the message was sent immediately,
     * false otherwise if queue is true, it has been queued, if it
     * is false it has been discarded.
     */

    public static boolean sendMessage(BungeeMessage message, ServerInfo server, boolean queue) {
        return server.sendData("BlobLib", BungeeMessage.serialize(message), queue);
    }
}
