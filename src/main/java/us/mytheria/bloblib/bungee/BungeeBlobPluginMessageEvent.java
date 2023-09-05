package us.mytheria.bloblib.bungee;

import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Event;
import us.mytheria.bloblib.entities.message.BlobPluginMessage;

/**
 * @author anjoismysign
 * <p>
 */
public class BungeeBlobPluginMessageEvent extends Event {
    private final BlobPluginMessage message;
    private final Server sender;

    /**
     * Here a protocol example for BlobTycoon:
     * BlobTycoon would be a Bungee plugin aswell. While being
     * a Bungee plugin, it will listen. If message's key !equals
     * "BlobTycoon" return. Switch message's type in order
     * to know what to do with message's value.
     *
     * @param message    the message
     * @param connection the sender's connection
     */
    public BungeeBlobPluginMessageEvent(BlobPluginMessage message, Server connection) {
        this.message = message;
        this.sender = connection;
    }

    /**
     * @return Message
     */
    public BlobPluginMessage getMessage() {
        return message;
    }

    /**
     * @return Sender's connection
     */
    public Server getSender() {
        return sender;
    }
}