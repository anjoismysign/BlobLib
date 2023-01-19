package us.mytheria.bloblib.entities.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class BlobChatMessage extends SerialBlobMessage {
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

    @Override
    public BlobChatMessage modify(Function<String, String> function) {
        return new BlobChatMessage(function.apply(chat), getSound());
    }
}
