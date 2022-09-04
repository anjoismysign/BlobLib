package us.mytheria.bloblib.vault;

import org.bukkit.entity.Player;

public interface Vault {

    void addCash(Player player, double amount);

    void setCash(Player player, double amount);

    boolean hasCashAmount(Player player, double amount);

    double getCash(Player player);
}
