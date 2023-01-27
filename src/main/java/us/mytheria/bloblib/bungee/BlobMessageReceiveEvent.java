package us.mytheria.bloblib.bungee;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Event;
import us.mytheria.bloblib.entities.message.BungeeMessage;

import javax.annotation.Nullable;

/**
 * @author anjoismysign
 * <p>
 * @deprecated API is in early development
 * state and is highly probable to be subject
 * to change.
 */
@Deprecated
public class BlobMessageReceiveEvent extends Event {
    private final BungeeMessage message;
    private final Connection sender;

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
    public BlobMessageReceiveEvent(BungeeMessage message, Connection connection) {
        this.message = message;
        this.sender = connection;
    }

    /**
     * @return Message
     */
    public BungeeMessage getMessage() {
        return message;
    }

    /**
     * @return Sender's connection
     */
    public Connection getSender() {
        return sender;
    }

    /**
     * @return null if false, Server object otherwise
     */
    @Nullable
    public Server isSenderAServer() {
        if (getSender() instanceof Server server)
            return server;
        return null;
    }
}