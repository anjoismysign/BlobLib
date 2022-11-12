package us.mytheria.bloblib.entities.message;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class BlobActionBar implements BlobMessage {

    private String message;

    public BlobActionBar(String message) {
        this.message = message;
    }

    @Override
    public void send(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
}
