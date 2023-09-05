package us.mytheria.bloblib.bungee;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.mytheria.bloblib.BlobLibBungee;
import us.mytheria.bloblib.entities.message.BlobPluginMessage;

public class BungeeBlobPluginMessageEventListener implements Listener {
    private final BlobLibBungee main;

    public BungeeBlobPluginMessageEventListener() {
        this.main = BlobLibBungee.getInstance();
        main.getProxy().getPluginManager().registerListener(main, this);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        Connection connection = e.getSender();
        if (!(connection instanceof Server server))
            return;
        BlobPluginMessage message = BlobPluginMessage.deserialize(e.getData());
        if (message == null)
            return;
        main.getProxy().getPluginManager().callEvent(new BungeeBlobPluginMessageEvent(message, server));
    }
}
