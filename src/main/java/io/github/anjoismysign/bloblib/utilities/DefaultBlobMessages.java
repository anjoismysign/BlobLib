package io.github.anjoismysign.bloblib.utilities;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DefaultBlobMessages {

    static DefaultBlobMessages getInstance(){
        return BlobLibDefaultBlobMessages.INSTANCE;
    }

    /**
     * Sends a BlobMessage when the player doesn't have enough money
     * @param player The player that's missing money and the one that's being sent the message.
     * @param missingAmount The amount that the player is missing.
     * @param currency The currency (or null if it's the default/not specified currency)
     */
    void economyNotEnough(@NotNull Player player, double missingAmount, @Nullable String currency);

    /**
     * Sends a BlobMessage when a player doesn't have enough money, to a CommandSender
     * @param player The player that's missing the money.
     * @param missingAmount The amount that the player is missing.
     * @param currency The currency (or null if it's the default/not specified currency)
     * @param receiver The receiver of the BlobMessage.
     */
    void economyNotEnoughOthers(@NotNull Player player, double missingAmount, @Nullable String currency, @NotNull CommandSender receiver);
}
