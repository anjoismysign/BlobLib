package us.mytheria.bloblib.bungee;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.mytheria.bloblib.BlobLibBungee;
import us.mytheria.bloblib.entities.message.BungeeMessage;

public class PluginMessageEventListener implements Listener {
    private BlobLibBungee main;

    public PluginMessageEventListener() {
        this.main = BlobLibBungee.getInstance();
        main.getProxy().getPluginManager().registerListener(main, this);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        if (!e.getTag().equals("BlobLib"))
            return;
        Connection connection = e.getSender();
        if (!(connection instanceof Server))
            return;
        BungeeMessage message = BungeeMessage.deserialize(e.getData());
        if (message == null)
            return;
        main.getProxy().getPluginManager().callEvent(new BlobMessageReceiveEvent(message, connection));
    }
}
