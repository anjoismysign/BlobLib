package us.mytheria.bloblib.entities.message;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlobChatActionbarTitleMessage extends BlobChatMessage {
    private final String title, subtitle, actionbar;
    private final int fadeIn, stay, fadeOut;

    public BlobChatActionbarTitleMessage(String chat, String actionbar, String title, String subtitle, int fadeIn, int stay, int fadeOut,
                                         BlobSound sound) {
        super(chat, sound);
        this.actionbar = actionbar;
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
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbar));
    }

    @Override
    public void toCommandSender(CommandSender commandSender) {
        if (commandSender instanceof Player player)
            sendAndPlay(player);
        else {
            commandSender.sendMessage(chat);
            commandSender.sendMessage(title);
            commandSender.sendMessage(subtitle);
            commandSender.sendMessage(actionbar);
        }
    }
}
