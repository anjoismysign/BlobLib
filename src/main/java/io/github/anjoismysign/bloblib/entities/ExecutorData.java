package io.github.anjoismysign.bloblib.entities;

import org.bukkit.command.CommandSender;

public record ExecutorData(BlobExecutor executor, String[] args, CommandSender sender) {
}
