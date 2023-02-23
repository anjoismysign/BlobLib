package us.mytheria.bloblib.vault.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class FoundEco implements VaultEconomyWorker {
    private final Economy economy;

    public FoundEco(Economy economy) {
        this.economy = economy;
    }

    @Override
    public void addCash(Player player, double amount) {
        economy.depositPlayer(player, amount);
    }

    @Override
    public void withdrawCash(Player player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

    @Override
    public void setCash(Player player, double amount) {
        economy.withdrawPlayer(player, economy.getBalance(player));
        if (amount > 0.01) {
            economy.depositPlayer(player, amount);
            return;
        }
        if (amount < -0.01) {
            amount = -amount;
            economy.withdrawPlayer(player, amount);
        }
    }

    @Override
    public boolean hasCashAmount(Player player, double amount) {
        return economy.has(player, amount);
    }

    @Override
    public double getCash(Player player) {
        return economy.getBalance(player);
    }
}
