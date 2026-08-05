package io.github.anjoismysign.bloblib.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public record TabCompleterData(CommandSender sender, String[] args, List<String> suggestions) {
}
