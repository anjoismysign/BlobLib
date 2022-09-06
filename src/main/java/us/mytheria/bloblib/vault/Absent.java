package us.mytheria.bloblib.vault;

import org.bukkit.entity.Player;

public class Absent implements Vault {

    @Override
    public void addCash(Player player, double amount) {
    }

    @Override
    public void withdrawCash(Player player, double amount) {
    }

    @Override
    public void setCash(Player player, double amount) {
    }

    @Override
    public boolean hasCashAmount(Player player, double amount) {
        return false;
    }

    @Override
    public double getCash(Player player) {
        return 0;
    }
}
