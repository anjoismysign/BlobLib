package io.github.anjoismysign.bloblib.entities.currency;

import io.github.anjoismysign.bloblib.api.BlobLibMessageAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record BlobEconomyCommandContext<T extends WalletOwner>(
        Currency currency,
        T walletOwner,
        Player player,
        double amount) {

    @Nullable
    public static <T extends WalletOwner> BlobEconomyCommandContext<T> WITH_AMOUNT(BlobEconomyCommand<T> command, String[] args, CommandSender sender) {
        if (args.length < 3)
            return null;
        String inputPlayer = args[1];
        Player player = Bukkit.getPlayer(inputPlayer);
        if (player == null) {
            BlobLibMessageAPI.getInstance()
                    .getMessage("Player.Not-Found", sender)
                    .toCommandSender(sender);
            return null;
        }
        Optional<T> optional = command.walletOwnerManager.isWalletOwner(player);
        if (optional.isEmpty()) {
            BlobLibMessageAPI.getInstance()
                    .getMessage("Player.Not-Inside-Plugin-Cache", sender)
                    .toCommandSender(sender);
            return null;
        }
        T walletOwner = optional.get();
        String input = args[2];

        double amount;
        try {
            amount = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            BlobLibMessageAPI.getInstance()
                    .getMessage("Economy.Number-Exception", sender)
                    .toCommandSender(sender);
            return null;
        }
        Currency currency = command.walletOwnerManager.getDefaultCurrency();
        if (args.length == 4) {
            String inputCurrency = args[3];
            currency = command.currencyManager.getObject(inputCurrency);
            if (currency == null) {
                BlobLibMessageAPI.getInstance()
                        .getMessage("Currency.Not-Found", sender)
                        .toCommandSender(sender);
                return null;
            }
        }
        return new <T>BlobEconomyCommandContext<T>(currency, walletOwner, player, amount);
    }

    @Nullable
    public static <T extends WalletOwner> BlobEconomyCommandContext<T> WITHOUT_AMOUNT(BlobEconomyCommand<T> command, String[] args, CommandSender sender) {
        if (args.length < 2)
            return null;
        String inputPlayer = args[1];
        Player player = Bukkit.getPlayer(inputPlayer);
        if (player == null) {
            BlobLibMessageAPI.getInstance()
                    .getMessage("Player.Not-Found", sender)
                    .toCommandSender(sender);
            return null;
        }
        Optional<T> optional = command.walletOwnerManager.isWalletOwner(player);
        if (optional.isEmpty()) {
            BlobLibMessageAPI.getInstance()
                    .getMessage("Player.Not-Inside-Plugin-Cache", sender)
                    .toCommandSender(sender);
            return null;
        }
        T walletOwner = optional.get();
        Currency currency = command.walletOwnerManager.getDefaultCurrency();
        if (args.length == 3) {
            String inputCurrency = args[2];
            currency = command.currencyManager.getObject(inputCurrency);
            if (currency == null) {
                BlobLibMessageAPI.getInstance()
                        .getMessage("Currency.Not-Found", sender)
                        .toCommandSender(sender);
                return null;
            }
        }
        return new <T>BlobEconomyCommandContext<T>(currency, walletOwner, player, 0);
    }
}
