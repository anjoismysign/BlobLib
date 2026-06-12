package io.github.anjoismysign.bloblib.utilities;

import io.github.anjoismysign.bloblib.api.BlobLibEconomyAPI;
import io.github.anjoismysign.bloblib.entities.message.BlobMessage;
import io.github.anjoismysign.bloblib.managers.BlobLibConfigManager;
import io.github.anjoismysign.bloblib.vault.multieconomy.ElasticEconomy;
import net.milkbowl.vault.economy.IdentityEconomy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum BlobLibDefaultBlobMessages implements DefaultBlobMessages{
    INSTANCE;

    @Override
    public void economyNotEnough(@NotNull Player player, double missingAmount, @Nullable String currency) {
        String locale = player.getLocale();
        ElasticEconomy elasticEconomy = BlobLibEconomyAPI.getInstance().getElasticEconomy();
        IdentityEconomy economy = currency == null ? elasticEconomy.getDefault() : elasticEconomy.getImplementation(currency);
        BlobMessage.by("Economy.Not-Enough")
                .localize(locale)
                .modder()
                .replace("%display%", economy.format(missingAmount))
                .get()
                .handle(player);
    }

    @Override
    public void economyNotEnoughOthers(@NotNull Player player, double missingAmount, @Nullable String currency, @NotNull CommandSender receiver) {
        String locale = (receiver instanceof Player playerCommandSender) ? playerCommandSender.getLocale() : BlobLibConfigManager.getInstance().getConsoleLocale();
        ElasticEconomy elasticEconomy = BlobLibEconomyAPI.getInstance().getElasticEconomy();
        IdentityEconomy economy = currency == null ? elasticEconomy.getDefault() : elasticEconomy.getImplementation(currency);
        BlobMessage.by("Economy.Not-Enough-Others")
                .localize(locale)
                .modder()
                .replace("%player%", player.getName())
                .replace("%display%", economy.format(missingAmount))
                .get()
                .toCommandSender(receiver);
    }
}
