package us.mytheria.bloblib.vault.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class Found implements VaultEconomyWorker {
    private final Economy economy;

    public Found(Economy economy) {
        this.economy = economy;
    }

    @Override
    public String getName() {
        return economy.getName();
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

    @Override
    public String format(double amount) {
        return economy.format(amount);
    }
}
