package us.mytheria.bloblib.vault;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VaultManager {
    private Vault vault;
    private boolean vaultInstalled = false;

    public VaultManager() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
            vault = new Found();
            vaultInstalled = true;
        } else {
            vault = new Absent();
        }
    }

    public void addCash(Player player, double amount) {
        vault.addCash(player, amount);
    }

    public void withdrawCash(Player player, double amount) {
        vault.withdrawCash(player, amount);
    }

    public void setCash(Player player, double amount) {
        vault.setCash(player, amount);
    }

    public boolean hasCashAmount(Player player, double amount) {
        return vault.hasCashAmount(player, amount);
    }

    public double getCash(Player player) {
        return vault.getCash(player);
    }

    public boolean isVaultInstalled() {
        return vaultInstalled;
    }
}
