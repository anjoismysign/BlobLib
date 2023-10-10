package us.mytheria.bloblib.entities.currency;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public class EconomyPHExpansion<T extends WalletOwner> extends PlaceholderExpansion {
    private final WalletOwnerManager<T> ownerManager;

    public EconomyPHExpansion(WalletOwnerManager<T> ownerManager) {
        this.ownerManager = ownerManager;
    }

    public boolean canRegister() {
        return true;
    }

    public @NotNull String getAuthor() {
        return "anjoismysign";
    }

    public @NotNull String getIdentifier() {
        return ownerManager.getPlugin().getName().toLowerCase() + "economy";
    }

    public @NotNull String getVersion() {
        return "1.0.1";
    }

    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
        String[] split = identifier.split("_");
        if (split.length == 2) {
            Set<String> customCrypto = ownerManager.currencyDirector.getObjectManager().keys();
            if (!customCrypto.contains(split[0]))
                return "Invalid currency: " + split[0];
            Optional<T> optional = ownerManager.isWalletOwner(offlinePlayer.getUniqueId());
            if (optional.isEmpty())
                return "Invalid player: " + offlinePlayer.getName();
            T walletOwner = optional.get();
            Currency currency = ownerManager.currencyDirector.getObjectManager().getObject(split[0]);
            String subIdentifier = split[1];
            Player player = Bukkit.getPlayer(offlinePlayer.getUniqueId());
            switch (subIdentifier) {
                case "display" -> {
                    return currency.display(walletOwner.getBalance(currency));
                }
                case "balance" -> {
                    return walletOwner.getBalance(currency) + "";
                }
                case "displayName" -> {
                    if (player == null)
                        return currency.getDisplayName();
                    else
                        return currency.getDisplayName(player);
                }
                case "capitalizeDisplayName" -> {
                    String displayName;
                    if (player == null)
                        displayName = currency.getDisplayName();
                    else
                        displayName = currency.getDisplayName(player);
                    displayName = StringUtils.capitalize(displayName);
                }
                default -> {
                    return "Invalid sub-identifier: " + subIdentifier;
                }
            }
        }
        return null;
    }
}
