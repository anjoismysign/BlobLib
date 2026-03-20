package io.github.anjoismysign.bloblib.entities;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandData {

    public enum CommandDataSender {
        SERVER,
        PLAYER
    }

    private String command;
    private CommandDataSender sender;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public CommandDataSender getSender() {
        return sender;
    }

    public void setSender(CommandDataSender sender) {
        this.sender = sender;
    }
    private static final CommandSender CONSOLE_SENDER = Bukkit.createCommandSender(component->{});

    public void apply(Player player){
        if (sender == CommandDataSender.PLAYER){
            player.performCommand(command);
        } else {
            Bukkit.dispatchCommand(CONSOLE_SENDER, command.replace("%player%", player.getName()));
        }
    }
}
