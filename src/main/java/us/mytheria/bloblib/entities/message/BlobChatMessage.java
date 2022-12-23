package us.mytheria.bloblib.entities.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlobChatMessage extends BlobMessage {
    private final String message;

    public BlobChatMessage(String message, BlobSound sound) {
        super(sound);
        this.message = message;
    }

    @Override
    public void send(Player player) {
        player.sendMessage(message);
    }

    public void send(CommandSender sender) {
        sender.sendMessage(message);
    }

    @Override
    public void toCommandSender(CommandSender commandSender) {
        if (commandSender instanceof Player player)
            sendAndPlay(player);
        else
            commandSender.sendMessage(message);
    }
}
