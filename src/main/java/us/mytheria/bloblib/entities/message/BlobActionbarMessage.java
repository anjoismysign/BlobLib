package us.mytheria.bloblib.entities.message;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class BlobActionbarMessage extends BlobMessage {
    private final String actionbar;

    public BlobActionbarMessage(String message, BlobSound sound) {
        super(sound);
        this.actionbar = message;
    }

    public BlobActionbarMessage(String message) {
        this(message, null);
    }

    @Override
    public void send(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbar));
    }

    @Override
    public void toCommandSender(CommandSender commandSender) {
        if (commandSender instanceof Player player)
            sendAndPlay(player);
        else
            commandSender.sendMessage(actionbar);
    }

    @Override
    public BlobActionbarMessage modify(Function<String, String> function) {
        return new BlobActionbarMessage(function.apply(actionbar), getSound());
    }
}
