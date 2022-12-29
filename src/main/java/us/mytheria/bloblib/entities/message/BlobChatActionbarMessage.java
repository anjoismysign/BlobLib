package us.mytheria.bloblib.entities.message;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class BlobChatActionbarMessage extends BlobChatMessage {
    private final String actionbar;

    public BlobChatActionbarMessage(String chat, String actionbar, BlobSound sound) {
        super(chat, sound);
        this.actionbar = actionbar;
    }

    @Override
    public void send(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbar));
    }

    @Override
    public void toCommandSender(CommandSender commandSender) {
        if (commandSender instanceof Player player)
            sendAndPlay(player);
        else {
            commandSender.sendMessage(chat);
            commandSender.sendMessage(actionbar);
        }
    }

    @Override
    public BlobChatActionbarMessage modify(Function<String, String> function) {
        return new BlobChatActionbarMessage(function.apply(chat), function.apply(actionbar), getSound());
    }
}
