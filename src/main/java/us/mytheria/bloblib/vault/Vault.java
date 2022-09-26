package us.mytheria.bloblib.vault;

import org.bukkit.entity.Player;

public interface Vault {

    void addCash(Player player, double amount);

    void withdrawCash(Player player, double amount);

    void setCash(Player player, double amount);

    boolean hasCashAmount(Player player, double amount);

    double getCash(Player player);

    boolean addPermission(Player player, String permission);

    boolean removePermission(Player player, String permission);

    boolean addPermission(Player player, String permission, String world);

    boolean removePermission(Player player, String permission, String world);
}
