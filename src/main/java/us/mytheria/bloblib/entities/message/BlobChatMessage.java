package us.mytheria.bloblib.entities.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlobChatMessage extends BlobMessage {
    protected final String chat;

    public BlobChatMessage(String message, BlobSound sound) {
        super(sound);
        this.chat = message;
    }

    @Override
    public void send(Player player) {
        player.sendMessage(chat);
    }

    public void send(CommandSender sender) {
        sender.sendMessage(chat);
    }

    @Override
    public void toCommandSender(CommandSender commandSender) {
        if (commandSender instanceof Player player)
            sendAndPlay(player);
        else
            commandSender.sendMessage(chat);
    }
}
