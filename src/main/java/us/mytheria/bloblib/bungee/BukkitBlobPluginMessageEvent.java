package us.mytheria.bloblib.bungee;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import us.mytheria.bloblib.entities.message.BlobPluginMessage;

/**
 * @author anjoismysign
 * <p>
 */
public class BukkitBlobPluginMessageEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    private final BlobPluginMessage message;

    /**
     * Here a protocol example for BlobTycoon:
     * BlobTycoon would be a Bungee plugin aswell. While being
     * a Bungee plugin, it will listen. If message's key !equals
     * "BlobTycoon" return. Switch message's type in order
     * to know what to do with message's value.
     *
     * @param message the message
     */
    public BukkitBlobPluginMessageEvent(BlobPluginMessage message) {
        this.message = message;
    }

    /**
     * @return Message
     */
    public BlobPluginMessage getMessage() {
        return message;
    }
}