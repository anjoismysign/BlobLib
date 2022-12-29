package us.mytheria.bloblib.entities.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class BlobChatTitleMessage extends BlobChatMessage {
    private final String title, subtitle;
    private final int fadeIn, stay, fadeOut;

    public BlobChatTitleMessage(String chat, String title, String subtitle, int fadeIn, int stay, int fadeOut,
                                BlobSound sound) {
        super(chat, sound);
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    @Override
    public void send(Player player) {
        super.send(player);
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    @Override
    public void toCommandSender(CommandSender commandSender) {
        if (commandSender instanceof Player player)
            sendAndPlay(player);
        else {
            commandSender.sendMessage(chat);
            commandSender.sendMessage(title);
            commandSender.sendMessage(subtitle);
        }
    }

    @Override
    public BlobChatTitleMessage modify(Function<String, String> function) {
        return new BlobChatTitleMessage(function.apply(chat), function.apply(title), function.apply(subtitle),
                fadeIn, stay, fadeOut, getSound());
    }
}
