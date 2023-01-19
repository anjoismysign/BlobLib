package us.mytheria.bloblib.entities.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Function;

public interface BlobMessage {

    void send(Player player);

    void sendAndPlay(Player player);

    void toCommandSender(CommandSender commandSender);

    BlobSound getSound();

    BlobMessage modify(Function<String, String> function);
}
