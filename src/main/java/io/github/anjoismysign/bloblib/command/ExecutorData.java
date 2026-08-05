package io.github.anjoismysign.bloblib.command;

import org.bukkit.command.CommandSender;

public record ExecutorData(BlobExecutor executor, String[] args, CommandSender sender) {
}
