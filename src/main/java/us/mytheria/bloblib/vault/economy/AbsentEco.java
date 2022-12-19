package us.mytheria.bloblib.vault.economy;

import org.bukkit.entity.Player;

public class AbsentEco implements VaultEconomyWorker {

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
