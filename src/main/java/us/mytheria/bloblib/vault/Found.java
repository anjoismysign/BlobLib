package us.mytheria.bloblib.vault;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Found implements Vault {
    private Economy economy;

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public Found() {
        setupEconomy();
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
