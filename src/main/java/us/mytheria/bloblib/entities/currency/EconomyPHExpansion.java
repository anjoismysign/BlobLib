package us.mytheria.bloblib.entities.currency;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
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

    public String onRequest(OfflinePlayer player, String identifier) {
        String[] split = identifier.split("_");
        if (split.length == 2) {
            Set<String> customCrypto = ownerManager.currencyDirector.getObjectManager().keys();
            if (!customCrypto.contains(split[0]))
                return "Invalid currency: " + split[0];
            Optional<T> optional = ownerManager.isWalletOwner(player.getUniqueId());
            if (optional.isEmpty())
                return "Invalid player: " + player.getName();
            T walletOwner = optional.get();
            Currency currency = ownerManager.currencyDirector.getObjectManager().getObject(split[0]);
            String subIdentifier = split[1];
            switch (subIdentifier) {
                case "display" -> {
                    return currency.display(walletOwner.getBalance(currency));
                }
                case "balance" -> {
                    return walletOwner.getBalance(currency) + "";
                }
                default -> {
                    return "Invalid sub-identifier: " + subIdentifier;
                }
            }
        }
        return null;
    }
}
