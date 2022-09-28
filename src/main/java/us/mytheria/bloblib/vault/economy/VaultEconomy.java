package us.mytheria.bloblib.vault.economy;

import org.bukkit.entity.Player;

public interface VaultEconomy {

    void addCash(Player player, double amount);

    void withdrawCash(Player player, double amount);

    void setCash(Player player, double amount);

    boolean hasCashAmount(Player player, double amount);

    double getCash(Player player);
}
