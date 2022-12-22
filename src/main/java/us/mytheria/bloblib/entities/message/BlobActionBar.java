package us.mytheria.bloblib.entities.message;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class BlobActionBar extends BlobMessage {
    private final String message;

    public BlobActionBar(String message, BlobSound sound) {
        super(sound);
        this.message = message;
    }

    public BlobActionBar(String message) {
        this(message, null);
    }

    @Override
    public void send(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
}
