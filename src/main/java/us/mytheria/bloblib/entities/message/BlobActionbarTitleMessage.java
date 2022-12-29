package us.mytheria.bloblib.entities.message;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlobActionbarTitleMessage extends BlobTitleMessage {
    private final String actionbar;

    public BlobActionbarTitleMessage(String actionbar, String title, String subtitle,
                                     int fadeIn, int stay, int fadeOut, BlobSound sound) {
        super(title, subtitle, fadeIn, stay, fadeOut, sound);
        this.actionbar = actionbar;
    }

    @Override
    public void send(Player player) {
        super.send(player);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbar));
    }

    @Override
    public void toCommandSender(CommandSender commandSender) {
        if (commandSender instanceof Player player)
            sendAndPlay(player);
        else {
            commandSender.sendMessage(title);
            commandSender.sendMessage(subtitle);
            commandSender.sendMessage(actionbar);
        }
    }
}
