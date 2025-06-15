package io.github.anjoismysign.bloblib.entities;

import org.bukkit.command.CommandSender;

import java.util.List;

public record TabCompleterData(CommandSender sender, String[] args, List<String> suggestions) {
}
