package us.mytheria.bloblib.entities.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class BlobTitleMessage extends SerialBlobMessage {
    protected final String title, subtitle;
    protected final int fadeIn, stay, fadeOut;

    public BlobTitleMessage(String title, String subtitle, int fadeIn, int stay, int fadeOut,
                            BlobSound sound) {
        super(sound);
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    @Override
    public void send(Player player) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    @Override
    public void toCommandSender(CommandSender commandSender) {
        if (commandSender instanceof Player player)
            sendAndPlay(player);
        else {
            commandSender.sendMessage(title);
            commandSender.sendMessage(subtitle);
        }
    }

    @Override
    public BlobTitleMessage modify(Function<String, String> function) {
        return new BlobTitleMessage(function.apply(title), function.apply(subtitle), fadeIn, stay,
                fadeOut, getSound());
    }
}
