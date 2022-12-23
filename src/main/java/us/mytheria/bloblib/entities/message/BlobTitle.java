package us.mytheria.bloblib.entities.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlobTitle extends BlobMessage {
    private final String title, subtitle;
    private final int fadeIn, stay, fadeOut;

    public BlobTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut,
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
}
